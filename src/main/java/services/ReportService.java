package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Employee;
import models.Like;
import models.Report;
import models.validators.ReportValidator;



/**
 * 日報テーブルの操作に関わる処理を行うクラス
 * EntityManagerがDBとやりとりする
 */

public class ReportService extends ServiceBase{
    /**
     * 指定した従業員が作成した日報データを、指定されたページ数の一覧画面に表示する分取得しReportViewのリストで返却する
     * (山田太郎さんの日報データ一覧の3ページ目を表示したい※このPGでは1ページあたり最大15件表示するようにしている)
     * @param employee 従業員
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<ReportView> getMinePerPage(EmployeeView employee, int page){

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))//DTOの、日報のprimary key（id）の何番目から取得するか、というSQL文（例：1ページに最大15件表示する場合は、3ページ目を指定すると、15*（3-1）なのでid30番目から表示させる）
                .setMaxResults(JpaConst.ROW_PER_PAGE)//↑で指定したところから、最大何件まで取得するか、というSQL文
                .getResultList();//取得した指定分のデータをリストにする
        return ReportConverter.toViewList(reports);
    }

    /**
     * 指定した従業員が作成した日報データの件数を取得し、返却する
     * @param employee
     * @return 日報データの件数
     */
    public long countAllMine(EmployeeView employee) {//引数に指定する従業員をセット

        long count = (long)em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE,Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE,EmployeeConverter.toModel(employee))
                .getSingleResult();
        return count;

    }

    /**
     * 指定されたページ数の一覧画面に表示する日報データを取得し、ReportViewのリストで返却する
     * ↑の従業員指定しない版
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<ReportView> getAllPerPage(int page){

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL, Report.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    /**
     * 日報テーブルのデータの件数を取得し、返却する
     * @return データの件数
     */
    public long countAll() {
        long reports_count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT, Long.class)
                .getSingleResult();
        return reports_count;
    }

    /**
     * idを条件に取得したデータをReportViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    /**
     * 画面から入力された日報の登録内容を元にデータを1件作成し、日報テーブルに登録する
     * @param rv 日報の登録内容
     * @return バリデーションで発生したエラーのリスト
     */
    public List<String> create(ReportView rv){
        List<String> errors = ReportValidator.validate(rv);//まずバリデーション実行
        if(errors.size()== 0) {//エラーがなければ、日時を取得して、日報をテーブルに登録
            LocalDateTime ldt = LocalDateTime.now();
            rv.setCreatedAt(ldt);
            rv.setUpdatedAt(ldt);
            createInternal(rv);
        }
        //バリデーションで発生したエラーを返却
        return errors;
        }
    /**
     * 画面から入力された日報の登録内容を元に、日報データを登録する
     * @param rv 日報の更新内容
     * @return バリデーションで発生したエラーのリスト
     */
    public List<String> update(ReportView rv){
        List<String> errors = ReportValidator.validate(rv);
        if(errors.size() == 0) {
            //更新日時を現在時刻に設定
            LocalDateTime ldt = LocalDateTime.now();
            rv.setUpdatedAt(ldt);

            updateInternal(rv);
        }
        //バリデーションで発生したエラーを返却
        return errors;

    }
    /**
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Report findOneInternal(int id) {
        return em.find(Report.class, id);
    }

    /**
     * 日報データを1件登録する
     * @param rv 日報データ
     */
    private void createInternal(ReportView rv) {
        em.getTransaction().begin();
        em.persist(ReportConverter.toModel(rv));//persist=永続化=DBにレコードとして保存
        em.getTransaction().commit();

    }

    /**
     * 日報データを更新する
     * @param rv 日報データ
     */
    private void updateInternal(ReportView rv) {
        em.getTransaction().begin();
        Report r = findOneInternal(rv.getId());
        ReportConverter.copyViewToModel(r, rv);//rv(画面で入力した日報内容)をr（findOneInternalで取得した、DBの元々の日報に上書き）
        em.getTransaction().commit();
    }
    /**
     * ログイン従業員idとshow画面の日報idを条件にいいね登録を検索する
     * @param employee ログイン従業員
     * @param report いいね対象の日報
     */

    public Like likeFind(Employee employee,Report report) {
        Like l = null;
        try {

            // ログインしている従業員idと詳細を開いた日報を条件に1件取得する
            l = em.createNamedQuery(JpaConst.Q_LIKE_GET_BY_EMP_AND_REP,Like.class)
                    .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, employee)
                    .setParameter(JpaConst.JPQL_PARM_REPORT,report)
                    .getSingleResult();
        } catch (NoResultException ex){
        }
        return l ;//DBから取得した情報を返却


}
    /** employee_idとreport_idを条件に検索し、データが取得できるかどうかで認証結果を返却する
     * @param employee_id ログイン従業員
     * @param report_id showで開いた日報
     * @return 認証結果を返却す(成功:true 失敗:false)
     */
   public Boolean isLiked(Employee employee,Report report) {

       boolean isLiked = false; //そもそもはfalseを返すようにする
           Like l = likeFind(employee, report);//パラメータを元にDBからデータを検索してlにセット

           if (l != null && l.getId() != null) {//lの中身が空でない（引数と同じ内容のレコードを持ったLikeがDBに存在する）か確認

               //データが取得できた場合、認証成功
               isLiked = true;//認証できたときにtrueを返すようにする
           }

       //認証結果を返却する
       return isLiked;
   }

   /**
    * 指定した日報のいいね件数を取得し、返却する
    * @param report
    * @return その日報のいいねの件数
    */
   public long countAllMine(Report report) {//引数に指定する日報をセット

       long count = (long)em.createNamedQuery(JpaConst.Q_LIKE_COUNT_ALL_MINE,Long.class)
               .setParameter(JpaConst.JPQL_PARM_REPORT,report)
               .getSingleResult();
       return count;

   }

   public void likeCreate(Like l) {
       LocalDateTime ldt = LocalDateTime.now();
       l.setCreated_at(ldt);
       em.getTransaction().begin();
       em.persist(l);//persist=永続化=DBにレコードとして保存
       em.getTransaction().commit();
}

   public void likeDestroy(Like l) {
       em.getTransaction().begin();
       em.remove(l);       // データ削除
       em.getTransaction().commit();
   }
}