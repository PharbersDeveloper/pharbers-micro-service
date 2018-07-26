package module.common.checkExist

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports.DBObject

/**
  * Created by spark on 18-4-20.
  */
trait checkAttrExist extends checkExistTrait {
    val ckAttrExist: JsValue => DBObject
}
