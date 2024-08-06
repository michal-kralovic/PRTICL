package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.PrticlDatabaseUtil;
import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class PrticlFunctionsBase {
    public PrticlFunctionsBase() {
    }

    /**
     * Executes a {@link Session} action in a transaction.<br/>
     * Automatically handles rollbacks in case an exception occurs.
     * @param action An action to execute.
     */
    protected void transactify(Consumer<Session> action) {
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

    /**
     * Executes a {@link Session} action in a transaction, returning {@link R}.<br/>
     * Automatically handles rollbacks in case an exception occurs.
     * @param action An action to execute.
     */
    protected <R> R transactifyAndReturn(Function<Session, R> action) {
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