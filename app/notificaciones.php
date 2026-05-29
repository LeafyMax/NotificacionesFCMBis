<?php
function obtenerAccessToken() {
    $jsonKeyFile = '/home/diccionario-piqueras/firebase.json'; 
    
    if (!file_exists($jsonKeyFile)) {
        die("Error: No se encuentra el archivo firebase.json en " . $jsonKeyFile);
    }

    $json = json_decode(file_get_contents($jsonKeyFile), true);
    $header = ['alg' => 'RS256', 'typ' => 'JWT'];
    $now = time();
    $payload = [
        'iss' => $json['client_email'],
        'scope' => 'https://www.googleapis.com/auth/firebase.messaging',
        'aud' => $json['token_uri'],
        'iat' => $now,
        'exp' => $now + 3600
    ];

    function base64url_encode($data) {
        return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
    }

    $base64Header = base64url_encode(json_encode($header));
    $base64Payload = base64url_encode(json_encode($payload));
    $signature = '';
    openssl_sign($base64Header . "." . $base64Payload, $signature, $json['private_key'], 'SHA256');
    $jwt = $base64Header . "." . $base64Payload . "." . base64url_encode($signature);

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $json['token_uri']);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query([
        'grant_type' => 'urn:ietf:params:oauth:grant-type:jwt-bearer',
        'assertion' => $jwt
    ]));
    $response = json_decode(curl_exec($ch), true);
    curl_close($ch);
    return $response['access_token'];
}

$nombre = isset($_POST['nombre']) ? $_POST['nombre'] : 'Prueba';
$mensaje = isset($_POST['mensaje']) ? $_POST['mensaje'] : 'Hola mundo';

try {
    $accessToken = obtenerAccessToken();
    $url = "https://fcm.googleapis.com/v1/projects/notificacionesfcmftpmax/messages:send";

    $data = [
        "message" => [
            "topic" => "allUsers",
            "notification" => [
                "title" => $nombre,
                "body" => $mensaje
            ]
            // Eliminamos la sección 'android' con 'ic_noti' para evitar fallos
        ]
    ];

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        "Authorization: Bearer " . $accessToken,
        "Content-Type: application/json"
    ]);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    echo "Status: $httpCode | Response: $response";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
