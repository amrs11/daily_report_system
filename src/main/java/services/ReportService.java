package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
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

        List<Report> reports = em.createNamedQuery(JpaConst.Q_EMP_GET_ALL, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))//DBの、日報のprimary key（id）の何番目から取得するか、というSQL文
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
    public void createInternal(ReportView rv) {
        em.getTransaction().begin();
        em.persist(ReportConverter.toModel(rv));//persist=永続化=DBにレコードとして保存
        em.getTransaction().commit();

    }

    /**
     * 日報データを更新する
     * @param rv 日報データ
     */
    public void updateInternal(ReportView rv) {
        em.getTransaction().begin();
        Report r = findOneInternal(rv.getId());
        ReportConverter.copyViewToModel(r, rv);//rv(画面で入力した日報内容)をr（findOneInternalで取得した、DBの元々の日報に上書き）
        em.getTransaction().commit();
    }


}
