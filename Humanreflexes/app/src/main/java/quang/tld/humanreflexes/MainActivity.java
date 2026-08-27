package quang.tld.humanreflexes;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Khai báo biến đại diện cho View trên XML để tương tác trong Java
    private ConstraintLayout layoutMain; // Biến quản lý nền màn hình
    private TextView tvStatus;           // Biến quản lý dòng chữ thông báo

    // Định nghĩa tập hợp các trạng thái của game
    private enum GameState {
        START,   // Trạng thái chờ người chơi bấm để bắt đầu
        WAITING, // Trạng thái màn hình đỏ (đang đếm ngược thời gian ngẫu nhiên)
        READY    // Trạng thái màn hình xanh (yêu cầu người chơi bấm nhanh)
    }

    // Khởi tạo trạng thái mặc định ban đầu là START
    private GameState currentState = GameState.START;

    // Handler dùng để gửi công việc vào luồng giao diện chính (Main Looper) sau một khoảng thời gian trì hoãn
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Lưu lại thời điểm (tính bằng mili-giây) khi màn hình vừa chuyển sang màu xanh
    private long starTime = 0;

    // code sẽ được thực thi khi hết thời gian đếm ngược
    private Runnable changeColorRunable = new Runnable() {
        @Override
        public void run() {
            currentState = GameState.READY; // đổi trạng thái sang READY
            layoutMain.setBackgroundColor(Color.parseColor("#388E3C")); //đổi nền sang xanh lá
            tvStatus.setText("CHẠM VÀO!"); // yêu cầu hiện lên màn hình
            // Lấy thời gian hệ thống hiện tại làm mốc bắt đầu phản xạ
            starTime = System.currentTimeMillis();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Bật tính năng hiển thị tràn màn hình
        setContentView(R.layout.activity_main); // Gán giao diện XML cho Activity này

        // Căn chỉnh padding tự động để không bị che bởi thanh trạng thái (status bar) hoặc tai thỏ
        ViewCompat.setOnApplyWindowInsetsListener(layoutMain, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ id từ file activity_main.xml sang biến Java tương ứng
        layoutMain = findViewById(R.id.layoutMain);
        tvStatus = findViewById(R.id.tvStatus);
    }

    // chạy lượt chơi
    private void Start(){
        currentState = GameState.WAITING; // chuyển sang chạng thái chờ
        layoutMain.setBackgroundColor(Color.parseColor("#D32F2F")); // chuyển màn sang đỏ
        tvStatus.setText("ĐỢI MÀN HÌNH CHUYỂN MÀU XANH LÁ...");

        // tạo thời gian ramdom 2-5 giây
        int randomDelay = new Random().nextInt(3000) + 2000;

        // hẹn giờ changeColor sau khi randomDelay
        handler.postDelayed(changeColorRunable, randomDelay);
    }
}