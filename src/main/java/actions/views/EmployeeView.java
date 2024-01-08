package actions.views;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 従業員情報について画面の入力値・出力値を扱うViewモデル
 * (DBのテーブルの内容を加工して表示したり、画面からデータに加工してDBに登録したりする)
 * (そのような場合、例えば同じEmployeeのデータでも、画面とDBのデータ内容に違いが出るので、これは画面用のモデルクラス）
 *
 */
@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)



public class EmployeeView {
    /**
     * id（自動採番のやつ）
     */
    private Integer id;

    /**
     * 社員番号 //@Columnていうアノテーションが要らないんだね
     */
    private String code;

    /**
     * 氏名
     */
    private String name;

    /**
     * パスワード
     */
    private String password;

    /**
     * 管理者権限があるかどうか（一般：0、管理者：1）
     */
    private Integer adminFlag;

    /**
     *登録日時
     */
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    private LocalDateTime updatedAt;

    /**
     * 削除された従業員かどうか（現役：0、削除済み：1）
     */
    private Integer deleteFlag;

}
