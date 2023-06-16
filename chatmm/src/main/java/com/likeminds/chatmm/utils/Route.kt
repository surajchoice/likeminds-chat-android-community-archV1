package com.likeminds.chatmm.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailActivity

object Route {
    // todo:
    const val ROUTE_CHATROOM = "collabcard"
    const val ROUTE_CHATROOM_DETAIL = "chatroom_detail"
    const val ROUTE_BROWSER = "browser"
    const val ROUTE_MAIL = "mail"

    const val PARAM_SOURCE_CHATROOM_ID = "source_chatroom_id"
    const val PARAM_SOURCE_COMMUNITY_ID = "source_community_id"
    private const val PARAM_COHORT_ID = "cohort_id"

    // todo: removed profle routes
    fun getRouteIntent(
        context: Context,
        routeString: String,
        flags: Int = 0,
        source: String? = null,
        map: HashMap<String, String>? = null,
        deepLinkUrl: String? = null
    ): Intent? {
        val route = Uri.parse(routeString)
        var intent: Intent? = null
        when {
            route.host == ROUTE_CHATROOM -> {
                intent = getRouteToChatroom(
                    context,
                    getChatroomRouteWithExtraParameters(route, map),
                    source,
                    deepLinkUrl
                )
            }
            route.host == ROUTE_BROWSER -> {
                intent = getRouteToBrowser(route)
            }
            route.host == ROUTE_CHATROOM_DETAIL -> {
                intent = getRouteToChatRoomDetail(context, route, source, deepLinkUrl)
            }
            route.host == ROUTE_MAIL -> {
                intent = getRouteToMail(route)
            }
        }
        if (intent != null) {
            intent.flags = flags
        }
        return intent
    }

    //route://collabcard?collabcard_id=
    private fun getRouteToChatroom(
        context: Context,
        route: Uri,
        source: String?,
        deepLinkUrl: String?
    ): Intent {
        val chatroomId = route.getQueryParameter("collabcard_id")
        val sourceChatroomId = route.getQueryParameter(PARAM_SOURCE_CHATROOM_ID)
        val sourceCommunityId = route.getQueryParameter(PARAM_SOURCE_COMMUNITY_ID)
        val cohortId = route.getQueryParameter(PARAM_COHORT_ID)

        // todo:
        val builder = ChatroomDetailExtras.Builder()
//            .chatroomId(chatroomId.toString())
//            .source(source)
//            .sourceChatroomId(sourceChatroomId)
//            .sourceCommunityId(sourceCommunityId)
//            .cohortId(cohortId)

//        when (source) {
//            LMAnalytics.Sources.SOURCE_NOTIFICATION -> {
//                builder.fromNotification(true).sourceLinkOrRoute(route.toString())
//            }
//
//            LMAnalytics.Sources.SOURCE_DEEP_LINK -> {
//                builder.openedFromLink(true).sourceLinkOrRoute(deepLinkUrl)
//            }
//        }

        return ChatroomDetailActivity.getIntent(
            context,
            builder.build()
        )
    }

    /**
     * * This function is used to add extra parameters in route
     */
    private fun getChatroomRouteWithExtraParameters(
        route: Uri,
        extraParameters: HashMap<String, String>?,
    ): Uri {
        return if (extraParameters != null) {
            appendQueryParametersToRoute(route, extraParameters)
        } else {
            route
        }
    }

    private fun appendQueryParametersToRoute(uri: Uri, map: HashMap<String, String>): Uri {
        val query = uri.queryParameterNames
        val newUri = Uri.Builder()
            .scheme(uri.scheme)
            .authority(uri.authority)
            .path(uri.path)
        for (item in map) {
            newUri.appendQueryParameter(item.key, item.value)
        }
        for (item in query) {
            newUri.appendQueryParameter(item, uri.getQueryParameter(item))
        }
        return newUri.build()
    }

    private fun getRouteToBrowser(route: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(route.getQueryParameter("link")))
    }

    //route://chatroom_detail?chatroom_id=
    private fun getRouteToChatRoomDetail(
        context: Context,
        route: Uri,
        source: String?,
        deepLinkUrl: String?
    ): Intent {
        val chatroomId = route.getQueryParameter("chatroom_id")
        val conversationId = route.getQueryParameter("conversation_id")

        // todo:
        val builder = ChatroomDetailExtras.Builder()
//            .chatroomId(chatroomId.toString())
//            .conversationId(conversationId)
//            .source(source)

//        when (source) {
//            LMAnalytics.Sources.SOURCE_NOTIFICATION -> {
//                builder.fromNotification(true).sourceLinkOrRoute(route.toString())
//            }
//
//            LMAnalytics.Sources.SOURCE_DEEP_LINK -> {
//                builder.openedFromLink(true).sourceLinkOrRoute(deepLinkUrl)
//            }
//        }

        return ChatroomDetailActivity.getIntent(
            context,
            builder.build()
        )
    }

    //route://mail?to=<email>
    private fun getRouteToMail(route: Uri): Intent? {
        val sendTo = route.getQueryParameter("to")
        if (AuthValidator.isValidEmail(sendTo)) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$sendTo")
            return Intent.createChooser(intent, "Select an email client")
        }
        return null
    }
}