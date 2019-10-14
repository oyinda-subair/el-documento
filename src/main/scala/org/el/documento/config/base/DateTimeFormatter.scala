package org.el.documento.config.base

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{JsNull, JsString, JsValue, Reads, Writes}

object DateTimeFormatter {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  val jodaDateReads: Reads[DateTime] = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  val jodaDateWrites: Writes[DateTime] = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(d.toString())
  }

  val jodaDateOptWrites: Writes[Option[DateTime]] = new Writes[Option[DateTime]] {
    def writes(d: Option[DateTime]): JsValue = d match {
      case Some(dateTime) ⇒ JsString(dateTime.toString())
      case None ⇒ JsNull
    }
  }
}