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

    // các trạng thái của game
    private enum GameState {
        START,   // chờ người chơi bấm để bắt đầu
        WAITING, // màn hình đỏ (đang đếm ngược thời gian ngẫu nhiên)
        READY    // hình xanh (yêu cầu người chơi bấm lẹ)
    }

    // Khởi tạo trạng thái ban đầu là chờ người chơi bấm để bắt đầu
    private GameState currentState = GameState.START;

    // Handler dùng để gửi công việc vào luồng giao diện chính sau một khoảng thời gian trì hoãn
    private final Handler handler = new Handler(Looper.getMainLooper());

    // nhập thời điểm (tính bằng mili-giây) khi màn hình vừa chuyển sang màu xanh
    private long starTime = 0;

    // code sẽ được thực thi khi hết thời gian đếm ngược
    private Runnable changeColorRunable = new Runnable() {
        @Override
        public void run() {
            currentState = GameState.READY; // đổi trạng thái sang READY
            layoutMain.setBackgroundColor(Color.parseColor("#388E3C")); //đổi nền sang xanh lá
            tvStatus.setText("CHẠM VÀO!"); // yêu cầu hiện lên màn hình
            starTime = System.currentTimeMillis(); // Lấy thời gian hệ thống hiện tại làm mốc bắt đầu bấm
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Bật tính năng hiển thị tràn màn hình
        setContentView(R.layout.activity_main); // Gán giao diện XML cho Activity này

        // Ánh xạ id từ file activity_main.xml sang biến Java tương ứng
        layoutMain = findViewById(R.id.layoutMain);
        tvStatus = findViewById(R.id.tvStatus);

        // Căn chỉnh padding tự động để không bị che bởi thanh trạng thái (status bar) hoặc tai thỏ
        ViewCompat.setOnApplyWindowInsetsListener(layoutMain, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutMain.setOnClickListener(v -> {
            switch (currentState){
                case START: Start(); break; // Bắt đầu trò chơi
                case WAITING: handlerEarlyTap(); break; // nếu bấm quá sớm
                case READY: Finish(); break; // Bấm chính xác lúc màu xanh
            }
        });
    }

    private void handlerEarlyTap(){
        // Hủy bỏ việc đếm ngược (nếu không hủy, màn hình sẽ vẫn tự động đổi sang màu xanh sau đó)
        handler.removeCallbacks(changeColorRunable);
        // Đưa trạng thái về lại START
        currentState = GameState.START;
        // Đổi nền màn hình sang màu Xám để thông báo người chơi đã phạm quy
        layoutMain.setBackgroundColor(Color.parseColor("#75757575"));
        // Cập nhật thông báo lỗi
        tvStatus.setText("Quá sớm! Chạm để thử lại.");
    }

    // chạy lượt chơi
    private void Start(){
        currentState = GameState.WAITING; // chuyển sang chạng thái chờ
        layoutMain.setBackgroundColor(Color.parseColor("#D32F2F")); // chuyển màn sang đỏ
        tvStatus.setText("ĐỢI MÀN HÌNH CHUYỂN MÀU XANH LÁ...");

        // tạo thời gian ramdom 2-5 giây
        int randomDelay = new Random().nextInt(3000) + 2000;

        // đặt khoảnh khắc changeColor sau khi randomDelay
        handler.postDelayed(changeColorRunable, randomDelay);
    }

    // kết thúc lượt chơi
    private void Finish(){
        /* Thời gian phản xạ = Thời gian hệ thống hiện tại
        trừ Thời điểm màn hình vừa đổi sang màu xanh */
        long reactionTimeMs = System.currentTimeMillis() - starTime;
        double reactionTimeS = reactionTimeMs / 1000.0; // đổi mili giây sang giây

        String Rating; // xếp loại điểm
        if(reactionTimeMs < 180){
            Rating = "Siêu phản xạ";
        }else if(reactionTimeMs < 250){
            Rating = "Nhanh như chớp!";
        }else if(reactionTimeMs < 350){
            Rating = "Mức trung bình bình thường";
        } else {
            Rating = "Cần luyện tập thêm";
        }
        // Đưa trạng thái game trở về START để người chơi có thể chơi lại lượt mới
        currentState = GameState.START;

        // Đổi nền màn hình sang màu Xanh dương để thông báo hoàn thành
        layoutMain.setBackgroundColor(Color.parseColor("#1976D2"));

        // Hiển thị kết quả tính lên màn hình
        String resultTest = String.format("Thời gian: %.3f giây (%d ms)\nĐánh giá: %s \nChạm để chơi lại",
                 reactionTimeS, reactionTimeMs, Rating);
        tvStatus.setText(resultTest);
    }
}