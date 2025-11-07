
// var passphrase = "key...변경";
// var iv = CryptoJS.lib.WordArray.random(128/8);
// var key = CryptoJS.enc.Hex.parse(CryptoJS.SHA1(passphrase).toString().substring(0,32));
// console.log("key: ", key)
// var ct = CryptoJS.AES.encrypt(str, key, { iv: iv });
// var enc = iv.concat(ct.ciphertext).toString();
//
// console.log("enc: ", enc)
//
// console.log("Result: " + CryptoJS.AES.decrypt({
//         ciphertext: CryptoJS.enc.Hex.parse(enc.substring(32))
//     }, CryptoJS.enc.Hex.parse(CryptoJS.SHA1(passphrase).toString().substring(0,32)),
//     {
//         iv: CryptoJS.enc.Hex.parse(enc.substring(0,32)),
//     }).toString(CryptoJS.enc.Utf8));
//
//
// enc = "B8160A9EDCA2CFECE3E6444BFDE09B780D8DD2D873B93080E7F5A6F4B5644217";
// console.log("JAVA 에서 암호화한 문자열 복호화 Result: " + CryptoJS.AES.decrypt({
//         ciphertext: CryptoJS.enc.Hex.parse(enc.substring(32))
//     }, CryptoJS.enc.Hex.parse(CryptoJS.SHA1(passphrase).toString().substring(0,32)),
//     {
//         iv: CryptoJS.enc.Hex.parse(enc.substring(0,32)),
//     }).toString(CryptoJS.enc.Utf8));

const encrypt = function(value) {
    const key128Bits = CryptoJS.PBKDF2(CRYPTO_CONFIG.secretKey, CryptoJS.enc.Hex.parse(CRYPTO_CONFIG.salt), {
        keySize: CRYPTO_CONFIG.keySize / 8,
        iterations: CRYPTO_CONFIG.iterationCount
    });

    const encrypted = CryptoJS.AES.encrypt(value, key128Bits, {
        keySize: CRYPTO_CONFIG.keySize,
        iv: CryptoJS.enc.Hex.parse(CRYPTO_CONFIG.iv),
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });

    return encrypted.toString();
}

const decrypt = function(value) {
    // const key128Bits = CryptoJS.PBKDF2(CRYPTO_CONFIG.secretKey, CryptoJS.enc.Hex.parse(CRYPTO_CONFIG.salt), {
    //     keySize: CRYPTO_CONFIG.keySize / 32,
    //     iterations: CRYPTO_CONFIG.iterationCount
    // });
    //
    // const decrypted = CryptoJS.AES.decrypt(value, key128Bits, {
    //     keySize: CRYPTO_CONFIG.keySize,
    //     iv: CryptoJS.enc.Hex.parse(CRYPTO_CONFIG.iv),
    //     mode: CryptoJS.mode.CBC,
    //     padding: CryptoJS.pad.Pkcs7
    // });

    var key = CryptoJS.enc.Utf8.parse(CRYPTO_CONFIG.secretKey);
    var iv = CryptoJS.enc.Utf8.parse(CRYPTO_CONFIG.secretKey.substring(0, 16));

    /*-- Encryption --*/
    // var cipherText = CryptoJS.AES.encrypt("{\"usrId\":\"U202411001C07\",\"loginId\":\"jiyoung@ideait.co.kr\",\"admin\":true,\"pjtNo\":null,\"cntrctNo\":null,\"selected\":false,\"departments\":null}", key, {
    //     iv: iv,
    //     mode: CryptoJS.mode.CBC,
    //     padding: CryptoJS.pad.Pkcs7
    // }).toString();
    // console.log(cipherText);

    /*-- Decryption --*/
    // var ddd = CryptoJS.AES.decrypt(cipherText, key, {
    //     iv: iv,
    //     mode: CryptoJS.mode.CBC,
    //     padding: CryptoJS.pad.Pkcs7
    // });
    // console.log(ddd.toString(CryptoJS.enc.Utf8))

    const decrypted = CryptoJS.AES.decrypt(value, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });

    return decrypted.toString(CryptoJS.enc.Utf8);
}