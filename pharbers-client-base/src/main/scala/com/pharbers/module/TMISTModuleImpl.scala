package com.pharbers.module

import javax.inject.Singleton
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.driver.util.redis_conn_cache
import com.pharbers.dbManagerTrait.dbInstanceManager

@Singleton
class DBManagerModule extends dbInstanceManager

@Singleton
class MAXRedisManager extends redis_conn_cache with PhRedisDriverImpl


