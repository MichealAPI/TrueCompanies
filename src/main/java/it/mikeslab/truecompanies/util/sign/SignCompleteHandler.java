package it.mikeslab.truecompanies.util.sign;

@FunctionalInterface
public interface SignCompleteHandler {
    void onSignClose(SignCompletedEvent event);
}
