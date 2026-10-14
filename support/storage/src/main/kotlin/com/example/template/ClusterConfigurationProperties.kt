package com.example.template

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.datasource.redis.cluster")
class ClusterConfigurationProperties(
    val nodes: List<String>
)