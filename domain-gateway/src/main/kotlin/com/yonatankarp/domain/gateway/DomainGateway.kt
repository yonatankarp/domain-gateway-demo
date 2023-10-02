package com.yonatankarp.domain.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DomainGatewayApplication

fun main(args: Array<String>) {
    runApplication<DomainGatewayApplication>(*args)
}
