INSERT INTO oauth_client_details (
  client_id, resource_ids, client_secret, scope, authorized_grant_types,
  web_server_redirect_uri, authorities, access_token_validity,
  refresh_token_validity, additional_information, autoapprove)
VALUES (
  'resource_server', '', 'abc123', 'read_profile,write_profile',
  'authorization_code', 'http://localhost:9000/callback', 'introspection',
  null, null, null, '');
