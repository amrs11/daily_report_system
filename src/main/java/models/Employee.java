package models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 *
 * 従業員データのDTOモデル（DBのレコードをJavaプログラムのデータとして扱えるようにする）
 * enumファイルの、JpaConstから引用されたJPQLが使用されている
 *
 */

@Table(name = JpaConst.TABLE_EMP)
@NamedQueries({//全4つのSELECT文。指定した内容をDBから取得して一覧として表示される
    @NamedQuery(
            name = JpaConst.Q_EMP_GET_ALL,//クエリの名前。コントローラーのほうでメソッド名として指定する用
            query = JpaConst.Q_EMP_GET_ALL_DEF),//クエリの実行内容。JPQL。
    @NamedQuery(
            name = JpaConst.Q_EMP_COUNT,
            query = JpaConst.Q_EMP_COUNT_DEF),
    @NamedQuery(
            name = JpaConst.Q_EMP_COUNT_REGISTERED_BY_CODE,
            query = JpaConst.Q_EMP_COUNT_REGISTERED_BY_CODE_DEF),
    @NamedQuery(
            name = JpaConst.Q_EMP_GET_BY_CODE_AND_PASS,
            query = JpaConst.Q_EMP_GET_BY_CODE_AND_PASS_DEF)
})

@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok) 便利なんだが
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)
@Entity
public class Employee {

    /**
     * DBのカラム、レコードを、Javaのフィールドに対応させる記述
     */

    /**
     * id
     */
    @Id
    @Column(name = JpaConst.EMP_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//primary key自動生成
    private Integer id;

    /**
     * 社員番号
     */
    @Column (name = JpaConst.EMP_COL_CODE,nullable = false , unique = true)
    private String code;//なんでIntegerじゃないんだろう アルファベットとかが入る可能性もあるから？

    /**
     * 氏名
     */
    @Column (name = JpaConst.EMP_COL_NAME, nullable = false)
    private String name;

    /**
     * パスワード
     */
    @Column (name = JpaConst.EMP_COL_PASS, length = 64, nullable = false)
    private String password;

    /**
     * 管理者権限があるかどうか（一般：０、管理者：１）
     */
    @Column(name = JpaConst.EMP_COL_ADMIN_FLAG,nullable = false)
    private Integer adminFlag;

    /**
     * 登録日時
     */
    @Column(name = JpaConst.EMP_COL_CREATED_AT,nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = JpaConst.EMP_COL_UPDATED_AT,nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 削除された従業員かどうか（現役：０、削除済み：１）
     */
    @Column(name = JpaConst.EMP_COL_DELETE_FLAG,nullable = false)
    private Integer deleteFlag;

}
