package com.example.dnbn_friend.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.dnbn_friend.model.ConsultationRequest

object ConsultationService {
    // 임시 전송 방식: 이메일 → 공유 시트 폴백 (백엔드 연동 전까지)
    fun submitViaEmail(context: Context, toEmail: String, request: ConsultationRequest) {
        val subject = "[상담신청] 매장:${request.storeId} 기종:${request.phoneId ?: "미선택"}"
        val body = buildString {
            appendLine("상담 요청이 접수되었습니다.")
            appendLine("- 사용자: ${request.userId}")
            appendLine("- 매장: ${request.storeId}")
            appendLine("- 선택 기종: ${request.phoneId ?: "미선택"}")
            appendLine("- 초기 설문: ${request.initialSurvey}")
            appendLine("- 공시 설문: ${request.subsidySurvey}")
            appendLine("- 앱 버전: ${request.appVersion ?: "N/A"}")
            appendLine("- 위치: ${request.userLocation ?: "N/A"}")
            appendLine("- 시각: ${request.timestamp}")
        }
        // 1) 기본: 이메일 앱으로 전송
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${toEmail}")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pm = context.packageManager
        if (emailIntent.resolveActivity(pm) != null) {
            context.startActivity(emailIntent)
            return
        }

        // 2) 폴백: 일반 공유 시트
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(shareIntent, "상담 요청 전송")
        if (shareIntent.resolveActivity(pm) != null) {
            context.startActivity(chooser)
            return
        }

        // 3) 최종 폴백: 안내 토스트
        Toast.makeText(context, "전송 가능한 앱이 없습니다. 관리자에게 문의해주세요.", Toast.LENGTH_LONG).show()
    }
}


