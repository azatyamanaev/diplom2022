package ru.itis.glabplugin.config;

import com.intellij.openapi.diagnostic.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 19.05.2022
 *
 * @author Azat Yamanaev
 */
public class TokenConfig {

    private static final com.intellij.openapi.diagnostic.Logger logger = Logger.getInstance(TokenConfig.class);


    private static final Lock saveLock = new ReentrantLock();


//    public static void saveToken(GitProject gitProject, String token, TokenType tokenType) {
//        saveLock.lock();
//        final String passwordKey = tokenType == TokenType.PERSONAL ? gitProject.getHost() : gitProject.getRemote();
//        logger.debug("Saving token with length ", (token == null ? 0 : token.length()), (tokenType == TokenType.PERSONAL ? " for host " : " for remote "), passwordKey);
//        final CredentialAttributes credentialAttributes = new CredentialAttributes(GitlabService.ACCESS_TOKEN_CREDENTIALS_ATTRIBUTE + passwordKey, passwordKey);
//        PasswordSafe.getInstance().setPassword(credentialAttributes, Strings.emptyToNull(token));
//        if (tokenType == TokenType.PERSONAL) {
//            //Delete token saved for this remote as it's now superseded by the personal access token
//            PasswordSafe.getInstance().setPassword(new CredentialAttributes(GitlabService.ACCESS_TOKEN_CREDENTIALS_ATTRIBUTE + gitProject.getRemote(), gitProject.getRemote()), null);
//        }
//        saveLock.unlock();
//    }
//
//    public static String getToken(GitProject gitProject) {
//        return getToken(gitProject.getRemote(), gitProject.getHost());
//    }
//
//    public static String getToken(String remote, String host) {
//        return getTokenAndType(remote, host).getLeft();
//    }
//
//    public static Pair<String, TokenType> getTokenAndType(String remote, String host) {
//        saveLock.lock();
//        TokenType tokenType;
//        final CredentialAttributes tokenCA = new CredentialAttributes(GitlabService.ACCESS_TOKEN_CREDENTIALS_ATTRIBUTE + remote, remote);
//        String token = PasswordSafe.getInstance().getPassword(tokenCA);
//
//        if (!Strings.isNullOrEmpty(token)) {
//            tokenType = TokenType.PROJECT;
//        } else {
//            //Didn't find a remote on token level, try host
//            final CredentialAttributes hostCA = new CredentialAttributes(GitlabService.ACCESS_TOKEN_CREDENTIALS_ATTRIBUTE + host, host);
//            token = PasswordSafe.getInstance().getPassword(hostCA);
//            tokenType = TokenType.PERSONAL;
//        }
//        if (Strings.isNullOrEmpty(token)) {
//            logger.debug("Found no token for remote ", remote, (host == null ? ":" : " and host " + host));
//            tokenType = null;
//        } else {
//            logger.debug("Found token with length ", token.length(), " for remote ", remote, (host == null ? ":" : " and host " + host));
//        }
//        saveLock.unlock();
//        return Pair.of(token, tokenType);
//    }
}
