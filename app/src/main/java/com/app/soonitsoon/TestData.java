package com.app.soonitsoon;

// 테스트 데이터 객체
// 객체 생성 법
// TestData td = new TestData(num);
// num = 1 -> 숭실대학교 정보과학관
// num = 2 -> 노량진역
// num = 3 -> 스타벅스 여의도점
// num = 4 -> 현대백화점 신촌점
public class TestData {
    public Loc loc;

    public TestData(int num) {
        loc = new Loc();

        if(num == 1) {
            loc.latitude = 37.49455;
            loc.longitude = 126.95979;
        }
        else if(num == 2) {
            loc.latitude = 37.513827;
            loc.longitude = 126.941525;
        }
        else if(num == 3) {
            loc.latitude = 37.524036;
            loc.longitude = 126.924489;
        }
        else if(num == 4) {
            loc.latitude = 37.555989;
            loc.longitude = 126.935708;
        }
        else {
            loc.latitude = 0;
            loc.longitude = 0;
        }
    }

    public void setTestLoc(double latitude, double longitude) {
        loc.latitude = latitude;
        loc.longitude = longitude;
    }

    public static class Loc {
        public double latitude;
        public double longitude;

        Loc() {
            latitude = 0;
            longitude = 0;
        }
    }
}