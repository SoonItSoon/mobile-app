package com.app.soonitsoon;

// 테스트 데이터 객체
// 객체 생성 법
// TestData td = new TestData(num);
public class TestData {
    public Loc loc;

    public TestData(int num) {
        loc = new Loc();

        if(num == 1) {
            setTestLoc("겨리집", 37.494312, 126.958674);
        }
        else if(num == 2) {
            setTestLoc("운수집", 37.599320, 127.027860);
        }
        else if(num == 3) {
            setTestLoc("수눅집", 37.248539, 127.025557);
        }
        else if(num == 4) {
            setTestLoc("에운집", 37.494398, 126.915473);
        }
        else if(num == 5) {
            setTestLoc("현쑤집", 37.531074, 126.900790);
        }
        else {
            loc.name = "";
            loc.latitude = 0;
            loc.longitude = 0;
        }
    }

    public void setTestLoc(String name, double latitude, double longitude) {
        loc.name = name;
        loc.latitude = latitude;
        loc.longitude = longitude;
    }

    public static class Loc {
        public String name;
        public double latitude;
        public double longitude;

        Loc() {
            name = "";
            latitude = 0;
            longitude = 0;
        }
    }
}