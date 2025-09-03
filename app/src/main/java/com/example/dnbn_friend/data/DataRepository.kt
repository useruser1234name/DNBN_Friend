package com.example.dnbn_friend.data

import com.example.dnbn_friend.model.*

object DataRepository {
    val phones = listOf(
        Phone(
            "1", "Galaxy S24 Ultra", "Samsung", 1598000,
            "https://via.placeholder.com/150",
            listOf("최고급 카메라 시스템", "S펜 지원", "대형 디스플레이"),
            "큰 화면", 5, listOf("사진/영상", "게임", "업무/공부"),
            "https://www.boxim.io"
        ),
        Phone(
            "2", "iPhone 15 Pro", "Apple", 1550000,
            "https://via.placeholder.com/150",
            listOf("A17 Pro 칩셋", "프로급 카메라", "티타늄 디자인"),
            "일반", 5, listOf("사진/영상", "영상", "SNS"),
            "https://www.boxim.io"
        ),
        Phone(
            "3", "Galaxy S24", "Samsung", 1155000,
            "https://via.placeholder.com/150",
            listOf("컴팩트한 디자인", "뛰어난 성능", "AI 기능"),
            "작고 가벼움", 4, listOf("SNS", "업무/공부", "영상"),
            "https://www.boxim.io"
        ),
        Phone(
            "4", "iPhone 15", "Apple", 1250000,
            "https://via.placeholder.com/150",
            listOf("다이나믹 아일랜드", "향상된 카메라", "올데이 배터리"),
            "일반", 4, listOf("SNS", "영상", "사진/영상"),
            "https://www.boxim.io"
        ),
        Phone(
            "5", "Galaxy A54", "Samsung", 598000,
            "https://via.placeholder.com/150",
            listOf("가성비 최고", "120Hz 디스플레이", "5000mAh 배터리"),
            "일반", 3, listOf("SNS", "영상", "업무/공부"),
            "https://www.boxim.io"
        ),
        Phone(
            "6", "Galaxy Z Fold6", "Samsung", 2232000,
            "https://via.placeholder.com/150",
            listOf("폴더블 디스플레이", "멀티태스킹 최적화", "프리미엄 디자인"),
            "큰 화면", 4, listOf("업무/공부", "영상", "게임"),
            "https://www.boxim.io"
        ),
        // ---- Apple (최근 5년 주요 모델) ----
        Phone("7", "iPhone 15 Pro Max", "Apple", 1900000,
            "https://via.placeholder.com/150",
            listOf("A17 Pro", "5배 망원", "ProMotion 120Hz"),
            "일반", 5, listOf("사진/영상", "영상", "게임"), "https://www.boxim.io"),
        Phone("8", "iPhone 15 Plus", "Apple", 1400000,
            "https://via.placeholder.com/150",
            listOf("대형 배터리", "알루미늄 프레임", "다이나믹 아일랜드"),
            "큰 화면", 4, listOf("영상", "SNS", "통화/문자"), "https://www.boxim.io"),
        Phone("9", "iPhone 14 Pro", "Apple", 1490000,
            "https://via.placeholder.com/150",
            listOf("A16 Bionic", "다이나믹 아일랜드", "ProMotion"),
            "일반", 5, listOf("사진/영상", "게임"), "https://www.boxim.io"),
        Phone("10", "iPhone 14", "Apple", 1250000,
            "https://via.placeholder.com/150",
            listOf("A15 Bionic", "향상된 카메라", "긴 배터리"),
            "일반", 4, listOf("SNS", "영상"), "https://www.boxim.io"),
        Phone("11", "iPhone 13 Pro", "Apple", 1390000,
            "https://via.placeholder.com/150",
            listOf("A15 Bionic", "ProMotion", "시네마틱 모드"),
            "일반", 5, listOf("사진/영상", "게임"), "https://www.boxim.io"),
        Phone("12", "iPhone 13", "Apple", 1090000,
            "https://via.placeholder.com/150",
            listOf("A15 Bionic", "듀얼 카메라", "밸런스 가격"),
            "일반", 4, listOf("SNS", "영상"), "https://www.boxim.io"),
        Phone("13", "iPhone 12 Pro", "Apple", 1290000,
            "https://via.placeholder.com/150",
            listOf("A14 Bionic", "LiDAR", "세련된 디자인"),
            "일반", 4, listOf("사진/영상", "업무/공부"), "https://www.boxim.io"),
        Phone("14", "iPhone 12", "Apple", 990000,
            "https://via.placeholder.com/150",
            listOf("A14 Bionic", "가벼운 무게", "밸런스"),
            "작고 가벼움", 4, listOf("SNS", "통화/문자"), "https://www.boxim.io"),

        // ---- Samsung Galaxy S 시리즈 ----
        Phone("15", "Galaxy S24+", "Samsung", 1350000,
            "https://via.placeholder.com/150",
            listOf("대화면", "AI 기능", "고성능"),
            "큰 화면", 5, listOf("영상", "업무/공부", "게임"), "https://www.boxim.io"),
        Phone("16", "Galaxy S23 Ultra", "Samsung", 1470000,
            "https://via.placeholder.com/150",
            listOf("200MP 카메라", "S펜", "고성능"),
            "큰 화면", 5, listOf("사진/영상", "업무/공부"), "https://www.boxim.io"),
        Phone("17", "Galaxy S22", "Samsung", 990000,
            "https://via.placeholder.com/150",
            listOf("깔끔한 디자인", "120Hz", "밸런스"),
            "일반", 4, listOf("SNS", "영상"), "https://www.boxim.io"),
        Phone("18", "Galaxy S21 Ultra", "Samsung", 1250000,
            "https://via.placeholder.com/150",
            listOf("망원 카메라", "고성능", "대형 배터리"),
            "큰 화면", 4, listOf("사진/영상", "게임"), "https://www.boxim.io"),

        // ---- Samsung Z / A 시리즈 ----
        Phone("19", "Galaxy Z Flip6", "Samsung", 1390000,
            "https://via.placeholder.com/150",
            listOf("플립 폼팩터", "컴팩트", "개성 디자인"),
            "작고 가벼움", 4, listOf("SNS", "영상"), "https://www.boxim.io"),
        Phone("20", "Galaxy Z Flip5", "Samsung", 1290000,
            "https://via.placeholder.com/150",
            listOf("커버 디스플레이", "폴더블", "경량"),
            "작고 가벼움", 4, listOf("SNS", "영상"), "https://www.boxim.io"),
        Phone("21", "Galaxy A55", "Samsung", 590000,
            "https://via.placeholder.com/150",
            listOf("가성비", "OLED 120Hz", "롱배터리"),
            "일반", 3, listOf("SNS", "영상", "업무/공부"), "https://www.boxim.io"),
        Phone("22", "Galaxy A35", "Samsung", 449000,
            "https://via.placeholder.com/150",
            listOf("가성비", "OLED", "안정성"),
            "일반", 3, listOf("SNS", "통화/문자"), "https://www.boxim.io"),

        // ---- Google Pixel ----
        Phone("23", "Pixel 8 Pro", "Google", 1399000,
            "https://via.placeholder.com/150",
            listOf("Tensor G3", "AI 편집", "우수한 카메라"),
            "일반", 5, listOf("사진/영상", "업무/공부"), "https://www.boxim.io"),
        Phone("24", "Pixel 8", "Google", 999000,
            "https://via.placeholder.com/150",
            listOf("Tensor G3", "가벼움", "7년 업데이트"),
            "작고 가벼움", 4, listOf("SNS", "사진/영상"), "https://www.boxim.io"),
        Phone("25", "Pixel 7", "Google", 799000,
            "https://via.placeholder.com/150",
            listOf("Tensor G2", "우수한 HDR", "가성비"),
            "일반", 4, listOf("사진/영상", "SNS"), "https://www.boxim.io"),
        Phone("26", "Pixel 6", "Google", 749000,
            "https://via.placeholder.com/150",
            listOf("Tensor G1", "AI 기능", "밸런스"),
            "일반", 4, listOf("SNS", "업무/공부"), "https://www.boxim.io"),

        // ---- Nothing ----
        Phone("27", "Nothing Phone (2)", "Nothing", 899000,
            "https://via.placeholder.com/150",
            listOf("Glyph 인터페이스", "경량 UI", "디자인"),
            "일반", 4, listOf("SNS", "영상"), "https://www.boxim.io"),
        Phone("28", "Nothing Phone (2a)", "Nothing", 499000,
            "https://via.placeholder.com/150",
            listOf("가성비", "유니크 디자인", "롱배터리"),
            "일반", 3, listOf("SNS", "통화/문자"), "https://www.boxim.io"),

        // ---- OnePlus ----
        Phone("29", "OnePlus 12", "OnePlus", 1199000,
            "https://via.placeholder.com/150",
            listOf("스냅드래곤 플래그십", "고속충전", "부드러운 UI"),
            "큰 화면", 5, listOf("게임", "영상"), "https://www.boxim.io"),
        Phone("30", "OnePlus 12R", "OnePlus", 799000,
            "https://via.placeholder.com/150",
            listOf("가성비 플래그십", "고속충전", "균형 잡힌 성능"),
            "일반", 4, listOf("게임", "SNS"), "https://www.boxim.io"),

        // ---- Xiaomi ----
        Phone("31", "Xiaomi 14", "Xiaomi", 1099000,
            "https://via.placeholder.com/150",
            listOf("라이카 협업 카메라", "고성능", "밸런스"),
            "일반", 5, listOf("사진/영상", "게임"), "https://www.boxim.io"),
        Phone("32", "Xiaomi 14 Ultra", "Xiaomi", 1599000,
            "https://via.placeholder.com/150",
            listOf("대형 센서", "프로 모드", "프리미엄"),
            "큰 화면", 5, listOf("사진/영상"), "https://www.boxim.io"),
        Phone("33", "Redmi Note 13", "Xiaomi", 399000,
            "https://via.placeholder.com/150",
            listOf("가성비", "대화면", "롱배터리"),
            "큰 화면", 3, listOf("SNS", "영상"), "https://www.boxim.io")
        ,
        // ---- 추가 보강: Apple ----
        Phone("34", "iPhone SE (3rd gen)", "Apple", 650000,
            "https://via.placeholder.com/150",
            listOf("A15 Bionic", "지문 인식", "경량"),
            "작고 가벼움", 3, listOf("통화/문자", "SNS"), "https://www.boxim.io"),
        Phone("35", "iPhone 13 mini", "Apple", 890000,
            "https://via.placeholder.com/150",
            listOf("컴팩트", "A15 Bionic", "가벼움"),
            "작고 가벼움", 4, listOf("SNS", "통화/문자"), "https://www.boxim.io"),

        // ---- 추가 보강: Samsung ----
        Phone("36", "Galaxy S23", "Samsung", 1050000,
            "https://via.placeholder.com/150",
            listOf("균형 성능", "120Hz", "가벼움"),
            "일반", 4, listOf("SNS", "영상"), "https://www.boxim.io"),
        Phone("37", "Galaxy S23+", "Samsung", 1190000,
            "https://via.placeholder.com/150",
            listOf("대화면", "롱배터리", "Snapdragon"),
            "큰 화면", 4, listOf("영상", "업무/공부"), "https://www.boxim.io"),
        Phone("38", "Galaxy S22 Ultra", "Samsung", 1290000,
            "https://via.placeholder.com/150",
            listOf("S펜", "망원 카메라", "프리미엄"),
            "큰 화면", 5, listOf("사진/영상", "업무/공부"), "https://www.boxim.io"),
        Phone("39", "Galaxy Z Fold5", "Samsung", 2090000,
            "https://via.placeholder.com/150",
            listOf("폴더블", "멀티태스킹", "대화면"),
            "큰 화면", 5, listOf("업무/공부", "영상"), "https://www.boxim.io"),
        Phone("40", "Galaxy A25", "Samsung", 349000,
            "https://via.placeholder.com/150",
            listOf("가성비", "OLED", "롱배터리"),
            "일반", 3, listOf("SNS", "통화/문자"), "https://www.boxim.io"),

        // ---- 추가 보강: Google ----
        Phone("41", "Pixel 7 Pro", "Google", 1099000,
            "https://via.placeholder.com/150",
            listOf("Tensor G2", "망원 카메라", "부드러운 UI"),
            "큰 화면", 5, listOf("사진/영상", "업무/공부"), "https://www.boxim.io"),
        Phone("42", "Pixel 6a", "Google", 449000,
            "https://via.placeholder.com/150",
            listOf("가성비", "Tensor G1", "경량"),
            "작고 가벼움", 3, listOf("SNS", "통화/문자"), "https://www.boxim.io"),

        // ---- 추가 보강: OnePlus ----
        Phone("43", "OnePlus 11", "OnePlus", 999000,
            "https://via.placeholder.com/150",
            listOf("스냅드래곤", "부드러운 UI", "빠른 충전"),
            "큰 화면", 5, listOf("게임", "영상"), "https://www.boxim.io"),

        // ---- 추가 보강: Xiaomi ----
        Phone("44", "Redmi Note 12", "Xiaomi", 349000,
            "https://via.placeholder.com/150",
            listOf("가성비", "대용량 배터리", "대화면"),
            "큰 화면", 3, listOf("SNS", "영상"), "https://www.boxim.io")
    )
    
    var stores = listOf(
        Store(
            "store1",
            "삼성 디지털프라자 강남점",
            "서울시 강남구 테헤란로 152",
            "02-555-1234",
            37.5002, 127.0367,
            listOf(
                PhoneSubsidy("1", "Galaxy S24 Ultra", 350000, "SKT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("1", "Galaxy S24 Ultra", 320000, "KT", planTier = "high", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("3", "Galaxy S24", 280000, "SKT", planTier = "mid", contractType = "MNP"),
                PhoneSubsidy("5", "Galaxy A54", 150000, "LG U+", planTier = "low", contractType = "NEW"),
                // 추가 기종/플랜/계약 유형 확장
                PhoneSubsidy("7", "iPhone 15 Pro Max", 370000, "KT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("7", "iPhone 15 Pro Max", 330000, "LG U+", planTier = "mid", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("2", "iPhone 15 Pro", 340000, "SKT", planTier = "high", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("16", "Galaxy S23 Ultra", 300000, "SKT", planTier = "mid", contractType = "MNP"),
                PhoneSubsidy("23", "Pixel 8 Pro", 260000, "KT", planTier = "mid", contractType = "NEW")
            )
        ),
        Store(
            "store2",
            "KT 강남직영점",
            "서울시 강남구 강남대로 396",
            "02-555-5678",
            37.4979, 127.0276,
            listOf(
                PhoneSubsidy("2", "iPhone 15 Pro", 320000, "KT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("4", "iPhone 15", 260000, "KT", planTier = "mid", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("1", "Galaxy S24 Ultra", 340000, "KT", planTier = "high", contractType = "NEW"),
                PhoneSubsidy("7", "iPhone 15 Pro Max", 360000, "KT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("9", "iPhone 14 Pro", 280000, "KT", planTier = "mid", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("23", "Pixel 8 Pro", 300000, "KT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("33", "Redmi Note 13", 120000, "KT", planTier = "low", contractType = "NEW")
            )
        ),
        Store(
            "store3",
            "SKT 강남본점",
            "서울시 강남구 역삼로 165",
            "02-555-9012",
            37.4967, 127.0382,
            listOf(
                PhoneSubsidy("1", "Galaxy S24 Ultra", 390000, "SKT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("1", "Galaxy S24 Ultra", 300000, "SKT", planTier = "mid", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("6", "Galaxy Z Fold6", 410000, "SKT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("3", "Galaxy S24", 260000, "SKT", planTier = "mid", contractType = "NEW"),
                PhoneSubsidy("16", "Galaxy S23 Ultra", 320000, "SKT", planTier = "high", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("15", "Galaxy S24+", 280000, "SKT", planTier = "mid", contractType = "MNP")
            )
        ),
        // LG U+ 직영점 추가
        Store(
            "store4",
            "LG U+ 강남직영점",
            "서울시 강남구 선릉로 425",
            "02-555-3456",
            37.5041, 127.0488,
            listOf(
                PhoneSubsidy("7", "iPhone 15 Pro Max", 350000, "LG U+", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("4", "iPhone 15", 240000, "LG U+", planTier = "mid", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("1", "Galaxy S24 Ultra", 330000, "LG U+", planTier = "high", contractType = "NEW"),
                PhoneSubsidy("6", "Galaxy Z Fold6", 390000, "LG U+", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("5", "Galaxy A54", 160000, "LG U+", planTier = "low", contractType = "NEW"),
                PhoneSubsidy("31", "Xiaomi 14", 180000, "LG U+", planTier = "mid", contractType = "MNP")
            )
        ),
        // 멀티 통신사 판매 매장 추가
        Store(
            "store5",
            "프리미엄 멀티통신샵 강남점",
            "서울시 강남구 도산대로 118",
            "02-555-7890",
            37.5235, 127.0291,
            listOf(
                PhoneSubsidy("7", "iPhone 15 Pro Max", 365000, "KT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("7", "iPhone 15 Pro Max", 355000, "SKT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("2", "iPhone 15 Pro", 310000, "LG U+", planTier = "mid", contractType = "DEVICE_CHANGE"),
                PhoneSubsidy("1", "Galaxy S24 Ultra", 345000, "KT", planTier = "high", contractType = "NEW"),
                PhoneSubsidy("16", "Galaxy S23 Ultra", 295000, "SKT", planTier = "mid", contractType = "NEW"),
                PhoneSubsidy("23", "Pixel 8 Pro", 305000, "KT", planTier = "high", contractType = "MNP"),
                PhoneSubsidy("27", "Nothing Phone (2)", 170000, "LG U+", planTier = "mid", contractType = "NEW")
            )
        )
    )
    
    var currentUser: User? = User(
        id = "user1",
        email = "test@example.com",
        name = "홍길동",
        age = 30,
        gender = "남성",
        phonePreferences = listOf("Samsung", "대형 화면", "카메라 중시")
    )
}