package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.PrticlDatabaseUtil;
import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class PrticlFunctionsBase {
    public PrticlFunctionsBase() {
    }

    protected void executeInTransaction(Consumer<Session> action) {
        try (var session = PrticlDatabaseUtil.getSession()) {
            var transaction = session.getTransaction();

            try {
                transaction.begin();
                action.accept(session);
                transaction.commit();

            } catch (Exception ex) {
                if (transaction.isActive())
                    transaction.rollback();

                throw ex;
            }
        }
    }

    protected <R> R executeInTransactionWithResult(Function<Session, R> action) {
        try (var session = PrticlDatabaseUtil.getSession()) {
            var transaction = session.getTransaction();

            try {
                transaction.begin();
                R result = action.apply(session);
                transaction.commit();
                return result;

            } catch (Exception ex) {
                if (transaction.isActive())
                    transaction.rollback();

                throw ex;
            }
        }
    }
}