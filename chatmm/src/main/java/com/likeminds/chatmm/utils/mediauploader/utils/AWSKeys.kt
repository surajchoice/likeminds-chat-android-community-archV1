package com.likeminds.chatmm.utils.mediauploader.utils

import com.likeminds.chatmm.BuildConfig

class AWSKeys {

    companion object {

        fun getIdentityPoolId(): String {
            return if (BuildConfig.DEBUG) {
                DebugKeys.IDENTITY_POOL_ID
            } else {
                ProdKeys.IDENTITY_POOL_ID
            }
        }

        fun getBucketName(): String {
            return if (BuildConfig.DEBUG) {
                DebugKeys.BUCKET_NAME
            } else {
                ProdKeys.BUCKET_NAME
            }
        }

        fun getBucketBaseUrl(): String {
            return if (BuildConfig.DEBUG) {
                DebugKeys.BUCKET_BASE_URL
            } else {
                ProdKeys.BUCKET_BASE_URL
            }
        }

        class DebugKeys {
            companion object {
                const val IDENTITY_POOL_ID =
                    "YXAtc291dGgtMToxODE5NjNiYS1mMmRiLTQ1MGItODE5OS05NjRhOTQxYjM4YzI="
                const val BUCKET_NAME = "YmV0YS1saWtlbWluZHMtbWVkaWE="
                const val BUCKET_BASE_URL =
                    "aHR0cHM6Ly9iZXRhLWxpa2VtaW5kcy1tZWRpYS5zMy5hbWF6b25hd3MuY29tLw=="
            }
        }

        class ProdKeys {
            companion object {
                const val IDENTITY_POOL_ID =
                    "YXAtc291dGgtMTpkNzNiYzJlZC1iZWRlLTQyYzgtYmFiNy0wYWJlMGEwMDEzMjU="
                const val BUCKET_NAME = "cHJvZC1saWtlbWluZHMtbWVkaWE="
                const val BUCKET_BASE_URL =
                    "aHR0cHM6Ly9wcm9kLWxpa2VtaW5kcy1tZWRpYS5zMy5hbWF6b25hd3MuY29tLw=="
            }
        }
    }
}