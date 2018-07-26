package module.common.datamodel

import play.api.libs.json.{JsArray, JsObject, JsValue}

trait SearchData {
	def searchJSValue(jv: JsValue)(key: String): String Map JsValue = {
		val temp = jv.as[JsObject].value.toMap
		if(temp.contains(key)) Map(key -> temp(key))
		else {
			temp.filter(f => f._2.isInstanceOf[JsObject] || f._2.isInstanceOf[JsArray]).flatMap (x =>
				x._2 match {
					case obj: JsObject => searchJSValue(obj.as[JsValue])(key)
					case array: JsArray => array.value.toList.map(searchJSValue(_)(key)).head
					case _: JsValue => ???
				}
			)
		}
	}
}
