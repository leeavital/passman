package com.leeavital.passman.data

/**
  * Created by lee on 8/28/16.
  */
package object codecs {


  import argonaut._
  import Argonaut._

  case class Vault(availableLogins: List[AvailableLogin]) {
    def addLogin(login: AvailableLogin): Vault = {
      Vault(login :: availableLogins)
    }

    def removeLogin(login: String): Vault = {
      Vault(availableLogins.filter(_.name != login))
    }
  }

  case class AvailableLogin(name: String, password: String)

  implicit def availableLoginCodec = casecodec2(AvailableLogin.apply, AvailableLogin.unapply)("name", "password")

  implicit def VaultCodec = casecodec1(Vault.apply, Vault.unapply)("availableLogins")
}

