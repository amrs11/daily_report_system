package models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * LikeデータのDTOモデル
 */

@Table(name = JpaConst.TABLE_LIKE)
@NamedQueries({
    @NamedQuery(
            name = JpaConst.Q_LIKE_COUNT_ALL_MINE,
            query = JpaConst.Q_LIKE_COUNT_ALL_MINE_DEF),
    @NamedQuery(
            name = JpaConst.Q_LIKE_GET_BY_EMP_AND_REP,
            query = JpaConst.Q_LIKE_GET_BY_EMP_AND_REP_DEF)
})

@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)
@Entity

public class Like {
    /**
     * id
     */
    @Id
    @Column(name = JpaConst.LIKE_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 日報にいいねした従業員
     */
    @ManyToOne
    @JoinColumn(name = JpaConst.LIKE_COL_EMP,nullable = false)
    private Employee employee;

    /**
     * いいねされた日報
     */
    @ManyToOne
    @JoinColumn(name = JpaConst.LIKE_COL_REP,nullable = false)
    private Report report;

    /**
     * 登録日時
     */
    @Column(name = JpaConst.LIKE_COL_CREATED_AT)
    private LocalDateTime createdAt;


}
