package src.system;


/************************************************

	フレームレート計測

 */
public class FrameRate {
    
    private long    basetime;   //測定基準時間
    private int     count;      //フレーム数
    private float   framerate;  //フレームレート
    
    //コンストラクタ
    public FrameRate() {
        basetime = System.currentTimeMillis();  //基準時間をセット
    }

    //フレームレートを取得
    public float getFrameRate() {
        return framerate;
    }

    //描画時に呼ぶ
    public void count() {
        ++count;        //フレーム数をインクリメント
        long now = System.currentTimeMillis();      //現在時刻を取得
        if (now - basetime >= 1000)
        {       //１秒以上経過していれば
            framerate = (float)(count * 1000) / (float)(now - basetime);        //フレームレートを計算
            basetime = now;     //現在時刻を基準時間に
            count = 0;          //フレーム数をリセット
        }
    }
}


