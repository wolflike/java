package com.pubnub.api.core;

import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.core.builder.SubscribeBuilder;
import com.pubnub.api.core.builder.UnsubscribeBuilder;
import com.pubnub.api.endpoints.History;
import com.pubnub.api.endpoints.Time;
import com.pubnub.api.endpoints.access.Audit;
import com.pubnub.api.endpoints.access.Grant;
import com.pubnub.api.endpoints.presence.*;
import com.pubnub.api.endpoints.pubsub.Publish;
import com.pubnub.api.endpoints.push.CreatePushNotification;
import com.pubnub.api.endpoints.push.ListProvisions;
import com.pubnub.api.endpoints.push.ModifyProvisions;
import com.pubnub.api.managers.BasePathManager;
import com.pubnub.api.managers.SubscriptionManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@Getter
@Slf4j
public class Pubnub {

    private PnConfiguration configuration;

    @Getter(AccessLevel.NONE)
    private SubscriptionManager subscriptionManager;
    @Getter(AccessLevel.NONE)
    private BasePathManager basePathManager;

    private String sdkVersion;

    public Pubnub(final PnConfiguration initialConfig) {
        this.configuration = initialConfig;
        this.subscriptionManager = new SubscriptionManager(this);
        this.basePathManager = new BasePathManager(initialConfig);

        sdkVersion = fetchSDKVersion();
    }

    public String getBaseUrl() {
        return this.basePathManager.getBasePath();
    }


    //
    public final void addListener(SubscribeCallback listener) {
        subscriptionManager.addListener(listener);
    }

    public final void removeListener(SubscribeCallback listener) {
        subscriptionManager.removeListener(listener);
    }

    public final SubscribeBuilder subscribe() {
        return new SubscribeBuilder(this.subscriptionManager);
    }

    public final UnsubscribeBuilder unsubscribe() { return new UnsubscribeBuilder(this.subscriptionManager); }

    // start push

    public final ModifyProvisions modifyPushProvisions() {
        return new ModifyProvisions(this);
    }

    public final ListProvisions listPushProvisions() {
        return new ListProvisions(this);
    }

    public final CreatePushNotification createPushNotification() {
        return new CreatePushNotification(this);
    }

    // end push

    public final WhereNow whereNow() {
        return new WhereNow(this);
    }
    public final HereNow hereNow() {
        return new HereNow(this);
    }

    public final Time time() {
        return new Time(this);
    }

    public final History history() { return new History(this); }


    public final Audit audit() {
        return new Audit(this);
    }
    public final Grant grant() {
        return new Grant(this);
    }

    public final GetState getPresenceState() {
        return new GetState(this);
    }
    public final SetState setPresenceState() {
        return new SetState(this, subscriptionManager);
    }

    public final Publish publish() {
        return new Publish(this);
    }

    // public methods

    /**
     * Perform Cryptographic decryption of an input string using cipher key provided by PNConfiguration
     * @param inputString String to be encrypted
     * @return String containing the encryption of inputString using cipherKey
     */
    public final String decrypt(String inputString) throws PubnubException {
        if (inputString == null) {
            throw new PubnubException(PubnubError.PNERROBJ_INVALID_ARGUMENTS);
        }

        return decrypt(inputString, this.getConfiguration().getCipherKey());
    }

    /**
     * Perform Cryptographic decryption of an input string using the cipher key
     * @param inputString String to be encrypted
     * @param cipherKey cipher key to be used for encryption
     * @throws PubnubException throws exception in case of failed encryption
     * @return String containing the encryption of inputString using cipherKey
     */
    public final String decrypt(final String inputString, final String cipherKey) throws PubnubException {
        if (inputString == null) {
            throw new PubnubException(PubnubError.PNERROBJ_INVALID_ARGUMENTS);
        }

        return new Crypto(cipherKey).decrypt(inputString);
    }

    /**
     * Perform Cryptographic encryption of an input string and the cipher key provided by PNConfiguration
     * @param inputString String to be encrypted
     * @return String containing the encryption of inputString using cipherKey
     */
    public final String encrypt(String inputString) throws PubnubException {
        if (inputString == null) {
            throw new PubnubException(PubnubError.PNERROBJ_INVALID_ARGUMENTS);
        }

        return encrypt(inputString, this.getConfiguration().getCipherKey());
    }

    /**
     * Perform Cryptographic encryption of an input string and the cipher key
     * @param inputString String to be encrypted
     * @param cipherKey cipher key to be used for encryption
     * @throws PubnubException throws exception in case of failed encryption
     * @return String containing the encryption of inputString using cipherKey
     */
    public final String encrypt(final String inputString, final String cipherKey) throws PubnubException {
        if (inputString == null) {
            throw new PubnubException(PubnubError.PNERROBJ_INVALID_ARGUMENTS);
        }

        return new Crypto(cipherKey).encrypt(inputString);
    }

    public String getVersion() {
        return sdkVersion;
    }

    /**
     * Stop the SDK and terminate all listeners.
     */
    public void stop() {
        subscriptionManager.stop();
    }

    private String fetchSDKVersion() {
        byte[] encoded;
        try {
            encoded = Files.readAllBytes(Paths.get(Pubnub.class.getClassLoader().getResource("version.properties").getPath()));
        } catch (IOException e) {
            return "N/A";
        }
        try {
            return new String(encoded, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            return "N/A";
        }
    }

}
