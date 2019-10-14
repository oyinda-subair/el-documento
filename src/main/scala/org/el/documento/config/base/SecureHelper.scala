package org.el.documento.config.base

import org.mindrot.jbcrypt.BCrypt

object SecureHelper {
  def hashPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }

  def confirmPassword(password: String, hashPassword: String): Boolean = {
    BCrypt.checkpw(password, hashPassword)
  }
}
