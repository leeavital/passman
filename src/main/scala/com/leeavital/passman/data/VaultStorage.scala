package com.leeavital.passman.data

import javax.crypto.spec.{PBEKeySpec, PBEParameterSpec}
import javax.crypto.{Cipher, SecretKeyFactory}

import argonaut.Argonaut._
import com.leeavital.passman.data.codecs.{Vault, _}

import scala.reflect.io.Path
import scalaz.{\/-, -\/}

object VaultStorage {
  def default: VaultStorage = {
    val vaultFile = Path.apply(Seq(System.getProperty("user.home"), ".passman.data")).toString
    new VaultStorage(vaultFile)
  }
}

/**
  * Created by lee on 8/28/16.
  */
class VaultStorage(vaultFile: String) {

  def createVaultFile(password: String): Unit = {
    val vault = Vault(Nil)
    writeVaultWithPassword(password, vault)
  }

  def readVaultFile(password: String): Secure[Vault] = {

    val byts: Secure[Array[Byte]] = for {
      bs <- IO.readFileBytes(vaultFile)
      decryptedBytes <- decryptBytesWithPassword(bs, password)
    } yield decryptedBytes

    byts.flatMap( bs => {
      new String(bs).decodeEither[Vault] match {
        case -\/(_) => CorruptedFile("could not parse JSON")
        case \/-(v) => Ok(v)
      }
    })
  }

  def addAvailableLogin(login: AvailableLogin, password: String): Secure[Unit] = {
    for {
      oldVault <- readVaultFile(password)
      newVault = oldVault.addLogin(login)
      written <- writeVaultWithPassword(password, newVault)
    } yield written
  }

  def deletePasswordByName(name: String, password: String): Secure[Unit] = {
    for {
      oldVault <- readVaultFile(password)
      newVault = oldVault.removeLogin(name)
      ok <- writeVaultWithPassword(password, newVault)
    } yield ok
  }

  private def writeVaultWithPassword(password: String, vault: Vault) = {
    val json = vault.asJson.toString
    val encryptedBytes = encryptBytesWithPassword(json.getBytes, password)
    IO.writeToFile(encryptedBytes, vaultFile)
  }

  private def generateSecretAndSpec(password: String, salt: String) = {
    val keySpec = new PBEKeySpec(password.toCharArray)
    val pbeParamSpec = new PBEParameterSpec(salt.getBytes, 20)
    val keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    val secret = keyFac.generateSecret(keySpec)
    (secret, pbeParamSpec)
  }

  private def encryptBytesWithPassword(bs: Array[Byte], password: String) = {
    val (secret, pbeParamSpec) = generateSecretAndSpec(password, "saltsalt")
    val cipher: Cipher = Cipher.getInstance("PBEWithMD5AndDES")
    cipher.init(Cipher.ENCRYPT_MODE, secret, pbeParamSpec)
    val byts = cipher.doFinal(bs)
    byts
  }

  private def decryptBytesWithPassword(bs: Array[Byte], password: String): Secure[Array[Byte]]= {
    try {
      val (secret, pbeParamSpec) = generateSecretAndSpec(password, "saltsalt")

      val cipher: Cipher = Cipher.getInstance("PBEWithMD5AndDES")
      cipher.init(Cipher.DECRYPT_MODE, secret, pbeParamSpec)

      val byts = cipher.doFinal(bs)
      Ok(byts)
    } catch {
      case (e: Exception) => {
        println(e)
        BadPassword
      }
    }
  }
}
