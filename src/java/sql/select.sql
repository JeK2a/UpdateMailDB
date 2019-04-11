SELECT
  (
    SELECT COUNT(*)
    FROM `a_api_email_folders`
    WHERE `status` = 'listening'
  ) AS `listening`,
  (
    SELECT COUNT(*)
    FROM `a_api_email_folders`
    WHERE `status` LIKE 'sleep%'
  ) AS `sleep`,
  (
    SELECT COUNT(*)
    FROM `a_api_email_folders`
    WHERE `status` LIKE 'close'
  ) AS `close`,
  (
    SELECT COUNT(*)
    FROM `a_api_email_folders`
    WHERE
      NOT (
        `status` = 'listening' OR
        `status` IS NULL OR
        `status` LIKE '%sleep%'
      )
  ) AS `another`,
  (
    SELECT COUNT(*)
    FROM `a_api_email_folders`
    WHERE `status` = NULL
  ) AS `null`,
  (
    SELECT COUNT(*)
    FROM `a_api_emails`
  ) AS `messages`;


SELECT
  COUNT(*),
  `status`,
  `account_email`,
  `folder_name`
FROM `a_api_email_folders`
GROUP BY `status`