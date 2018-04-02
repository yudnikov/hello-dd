package net.superkassa.exceptions

case class EnumFromStringException(string: String, clazz: Class[_])
  extends Exception(s"Enumeration value of ${clazz.getName} can't be extract from '$string'")