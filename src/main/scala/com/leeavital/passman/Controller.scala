package com.leeavital.passman

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

import com.leeavital.passman.data.codecs.{Vault, AvailableLogin}
import com.leeavital.passman.data._

case class AvailableLogins(logins: Seq[String])

case class AddLoginMessage(name: String, password: String)

/**
  * Created by lee on 8/27/16.
  */
class Controller(view: MainView) {

  val storage = VaultStorage.default

  var cachedPassword : Option[String] = None

  def tryUnlock(password: String): Unit = {
    cachedPassword = Some(password)
    storage.readVaultFile(password) match {
      case Ok(vault) =>
        view.onUnlockSucceeded

        val names = vault.availableLogins.map(_.name)
        val availableLogins = AvailableLogins(names)

        view.showUnlockedZeroState(availableLogins)

      case BadPassword =>
        view.onUnlockFailed

      case e@CorruptedFile(msg) =>
        println("HACK: CREATING A NEW VAULT BECAUSE IT WAS MISSING")
        storage.createVaultFile(password)
        tryUnlock(password)
    }
  }

  def selectAvailableLogin(str: String): Unit = {
    //TODO: handle cachedPassword being absent
    storage.readVaultFile(cachedPassword.get) match {
      case Ok(vault) =>
        vault.availableLogins.find(login => login.name == str) match {
          case Some(logon) =>
            val selection = new StringSelection(logon.password)
            Toolkit.getDefaultToolkit.getSystemClipboard.setContents(selection, selection)

            view.passwordWasCopied(str)

          case None =>
            throw new RuntimeException("could not find logoc with name " + str)
        }
      case e =>
        println(e)
        throw new RuntimeException("vault was no good!")
    }
  }

  def addLogin(addMessage: AddLoginMessage): Unit = {
    val AddLoginMessage(name, password) = addMessage

    val result: Secure[Vault] = for {
      addResult <- storage.addAvailableLogin(
        AvailableLogin(name, password),
        cachedPassword.get)

      reUnlockResult <- storage.readVaultFile(cachedPassword.get)
    } yield reUnlockResult

    result match {
      case Ok(vault) =>
        val logins = AvailableLogins(vault.availableLogins.map(_.name))
        view.showUnlockedZeroState(logins)

      case o@_ =>
        throw new RuntimeException("panic " + o)
    }
  }

  def deleteLogin(name: String): Unit = {
    val newVault = for {
      _ <- storage.deletePasswordByName(name, cachedPassword.get)
      vault <- storage.readVaultFile(cachedPassword.get)
    } yield vault

    newVault match {
      case Ok(vault) =>
        val logins = AvailableLogins(vault.availableLogins.map(_.name))
        view.showUnlockedZeroState(logins)

      case e@_ =>
        throw new RuntimeException("panic " + e)
    }
  }
}
