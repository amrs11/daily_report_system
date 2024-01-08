package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.ForwardConst;

/**
 *
 * エラー発生時の処理を行うActionクラス
 *
 */
public class UnknownAction extends ActionBase { //抽象クラス：ActionBaseを継承

    /**
     * 共通エラー画面「お探しのページは見つかりませんでした。」を表示する
     */
    @Override
    public void process() throws ServletException, IOException {

        //エラー画面を表示
        forward(ForwardConst.FW_ERR_UNKNOWN);
    }

}
