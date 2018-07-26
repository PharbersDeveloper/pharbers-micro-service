package com.pharbers.module

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.dbManagerTrait.dbInstanceManager

class TMISTModules extends Module{
	override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
		Seq(
			bind[dbInstanceManager].to[DBManagerModule]
			,bind[PhRedisDriverImpl].to[MAXRedisManager]
		)
	}
}
