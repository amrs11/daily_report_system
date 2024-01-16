package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;

/**
 * 従業員に関わる処理を行うActionクラス
 *
 */
public class EmployeeAction extends ActionBase {

    private EmployeeService service; // このクラスのメソッドで使用するEmployeeServiceのインスタンスを生成するため

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new EmployeeService(); // serviceインスタンス(DBのレコードとやりとりする役/emが中身)を生成

        //メソッドを実行
        invoke();

        service.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        //管理者かどうかのチェック
        if(checkAdmin()) {

        //指定されたページ数の一覧画面に表示するデータを取得
        int page = getPage();
        List<EmployeeView> employees = service.getPerPage(page);

        //全ての従業員データの件数を取得
        long employeeCount = service.countAll();

        putRequestScope(AttributeConst.EMPLOYEES, employees); //取得した従業員データ
        putRequestScope(AttributeConst.EMP_COUNT, employeeCount); //全ての従業員データの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_EMP_INDEX);
        }

    }
    /**
     * 新規登録画面を表示する（画面の表示だけ）
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException,IOException{

        //管理者かどうかのチェック
        if(checkAdmin()) {
        putRequestScope(AttributeConst.TOKEN,getTokenId()); //CSRF対策トークン
        putRequestScope(AttributeConst.EMPLOYEE,new EmployeeView());//空の従業員インスタンス（最初は入力ボックスを空にするため）

        //新規登録画面を表示
        forward(ForwardConst.FW_EMP_NEW);
    }
    }

    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */

    public void create() throws ServletException,IOException{


        // 管理者かどうか&CSRF対策のTokenチェック
        if(checkAdmin() && checkToken()){

            //パラメータの値（entryNewページで入力した情報）をもとに従業員情報のインスタンスを作成する
            EmployeeView ev = new EmployeeView( //引数ありコンストラクタを呼び出し(modelではLombokで自動生成されているやつ)
                    null,//自動採番のID
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,//登録日時
                    null,//更新日時
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
            //↑これらの情報がセットされたインスタンスevが生成される

            //アプリケーションスコープからpepper文字列を取得
            String pepper = getContextScope(PropertyConst.PEPPER);

            //従業員情報登録
            List<String> errors = service.create(ev,pepper);

            if(errors.size() > 0) {
                //登録中にエラーがあった場合(=errorsの中身が空じゃない)
                putRequestScope(AttributeConst.TOKEN,getTokenId());//CSRF対策用トークン
                putRequestScope(AttributeConst.EMPLOYEE,ev);//登録された従業員情報
                putRequestScope(AttributeConst.ERR,errors);//エラーのリスト

                //新規登録画面を再表示
                forward(ForwardConst.FW_EMP_NEW);

            } else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH,MessageConst.I_REGISTERED.getMessage());

                //一覧画面（Index）にリダイレクト
                redirect(ForwardConst.ACT_EMP,ForwardConst.CMD_INDEX);
            }


        }
        }

        /**
         * 詳細画面を表示する
         * @throws ServletException
         * @throws IOException
         */

        public void show() throws ServletException, IOException {

            //管理者かどうかのチェック
            if(checkAdmin()) {

            //idを条件に従業員データを取得する(em.find()→service.findInternal()→service.findOne()→リクエストパラメータから取得)
            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if(ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

                //データが取得できなかった、または論理削除されている場合はエラー画面を表示
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return;
            }

            putRequestScope(AttributeConst.EMPLOYEE,ev);//jspがをEL式でデータを取得できるように、evを"employee"にセットしてリクエストスコープに置く

            //詳細画面を表示
            forward(ForwardConst.FW_EMP_SHOW);


        }
        }

        /**
         * 編集画面を表示する
         * @throws ServletException
         * @throws IOException
         */

        public void edit() throws ServletException,IOException{

            //管理者かどうかのチェック
            if(checkAdmin()) {

            // idを条件に従業員データを取得する（）
            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if(ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

                //データが取得できなかった、または論理削除されている場合はエラー画面を表示
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return; //ここでreturnを記述することで、if文がTRUEの場合は↓の処理は行われずメソッド終了になる
            }

                putRequestScope(AttributeConst.TOKEN,getTokenId()); //CSRF対策用トークBン
                putRequestScope(AttributeConst.EMPLOYEE,ev); //取得した従業員情報

                //編集画面を表示する
                forward(ForwardConst.FW_EMP_EDIT);

            }
        }

        /**
         * 更新を行う
         * @throws ServleteException
         * @throws IOException
         */

        public void update() throws ServletException,IOException{
            // 管理者かどうか＆CSRF対策 tokenのチェック
            if(checkAdmin() && checkToken()) {
                // パラメータの値を元に従業員情報のインスタンスを生成する
                EmployeeView ev = new EmployeeView(//引数ありコンストラクタに値をセット
                        //_form.jspで、ボックスに入力された文字列を、それぞれnameで変数に入れているやつ
                        toNumber(getRequestParam(AttributeConst.EMP_ID)),
                        getRequestParam(AttributeConst.EMP_CODE),
                        getRequestParam(AttributeConst.EMP_NAME),
                        getRequestParam(AttributeConst.EMP_PASS),
                        toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                        null,
                        null,
                        AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

                //アプリケーションスコープからpepper文字列を取得
                String pepper = getContextScope(PropertyConst.PEPPER);

                //従業員情報更新
                List<String> errors = service.update(ev,pepper);

                if(errors.size() > 0) {
                    //更新中にエラーが発生した場合

                    putRequestScope(AttributeConst.TOKEN,getTokenId()); //CSRF対策用トークン
                    putRequestScope(AttributeConst.EMPLOYEE,ev);//入力された従業員情報
                    putRequestScope(AttributeConst.ERR,errors);//エラーのリスト

                    //編集画面を再表示
                    forward(ForwardConst.FW_EMP_EDIT);
                } else {
                    //更新中にエラーがなかった場合

                    //セッションに更新完了のフラッシュメッセージを設定
                    putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

                    //一覧画面にリダイレクト
                    redirect(ForwardConst.ACT_EMP,ForwardConst.CMD_INDEX);
                }

            }
            }
        /**
         * 論理削除を行う（実際にDBから消してしまわない）
         * @throws ServletException
         * @throws IOException
         */
        public void destroy() throws ServletException,IOException{


            //管理者かどうか&CSRF対策 tokenのチェック
            if(checkAdmin() && checkToken()) {

                //idを条件に従業員データを論理削除
                service.destroy(toNumber(getRequestParam(AttributeConst.EMP_ID)));

                //セッションに削除完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH,MessageConst.I_DELETED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
            }

        /**
         * ログイン中の従業員が管理者かどうかチェックし、管理者でなければエラー画面を表示
         * （管理者以外は、新規登録や更新、削除等ができないようにする）
         * true: 管理者 false:管理者ではない
         * @throws ServletException
         * @throws IOException
         */

        private boolean checkAdmin() throws ServletException,IOException{

            //セッションからログイン中の従業員情報を取得
            EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

            //管理者でなければエラー画面を表示&呼び出し元にfalseを返す
        if(ev.getAdminFlag() != AttributeConst.ROLE_ADMIN.getIntegerValue()) {

            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;
        } else {

            return true;
        }
        }

        }
