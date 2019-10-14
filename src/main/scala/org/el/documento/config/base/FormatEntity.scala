package org.el.documento.config.base

import org.joda.time.DateTime
import org.joda.time.format._
import play.api.libs.json._

trait FormatEntity[T] {
  val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-DD'T'HH:mm:ss.SSS'Z'").withZoneUTC()

  implicit val formatDateTime: Format[DateTime] = new Format[DateTime] {

    def reads(json: JsValue): JsResult[DateTime] =  json match {
      case JsNumber(ms)   ⇒ JsSuccess(new DateTime(ms.toLong))
      case JsString(str)  ⇒
        try {
          JsSuccess(DateTime.parse(str, dateFormat))
        }catch {
          case _: IllegalArgumentException => JsError(s"Expected a String representation of a date. Value '$str' does not look like one.")
        }
      case _              ⇒ JsError("String value expected")
    }

    def writes(d: DateTime): JsValue = JsString(d.toString()) //JsString(ts.toString)
  }
  implicit  val format: Format[T]
}
