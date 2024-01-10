package services;

import javax.persistence.EntityManager;

import utils.DBUtil;

/**
 * DB接続に関わる共通処理を行うクラス
 * 今まで各サーブレットてEntityManagerを生成してはcloseしていたが、このクラスを各serviceクラスが継承することでその処理がここに集約
 */

public class ServiceBase {

    /**
     * EntityManagerインスタンス
     */
    protected EntityManager em = DBUtil.createEntityManager();

    /**
     * EntityManagerのクローズ
     */

    public void close() {
        if(em.isOpen()) {
            em.close();
        }
    }

}
