[#ftl]
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Enonic CMS - Authentication</title>
    <link rel="stylesheet" href="_xtrace/resources/authentication-page.css" type="text/css" />
    <link rel="stylesheet" href="_xtrace/resources/authentication-page-ie.css" type="text/css" />
</head>
<body>

<table cellpadding="0" cellspacing="0" border="0" id="wrapper">
    <tr>
        <td>
            [#if authenticationFailed = true]
                <p class="cms-error">The username or password you entered is not valid</p>
            [/#if]

            <div id="inner">
                <h1>Authenticate</h1>
                <div id="form-container">
                    <form action="" method="post">
                        <input type="hidden" name="_xtrace_authentication" value="true"/>
                        <table cellspacing="0" cellpadding="0" border="0">
                            <tr>
                                <td class="label-container">
                                    <label for="userstore">Userstore</label>
                                </td>
                                <td class="input-container">
                                    <select name="_xtrace_userstore" id="userstore">
                                        [#list userStores?keys?sort as key]
                                            <option value="${key}">${userStores[key]}</option>
                                        [/#list]
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="label-container">
                                    <label for="username">Username</label>
                                </td>
                                <td class="input-container">
                                    <input type="text" id="username" name="_xtrace_username" value="" />
                                </td>

                            </tr>
                            <tr>
                                <td class="label-container">
                                    <label for="password">Password</label>
                                </td>
                                <td class="input-container">
                                    <input type="password" id="password" name="_xtrace_password" value="" />
                                </td>
                            </tr>
                            <tr>
                                <td class="label-container">
                                    <br />
                                </td>
                                <td class="input-container">
                                    <input type="submit" style="cursor: pointer;" class="button_text" name="login" value="Authenticate" id="login"/>

                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
            <p class="version">
                CMS ${version}
            </p>
        </td>
    </tr>
</table>

<script>
    document.getElementById( 'username' ).focus();
</script>

</body>
</html>
<html>
</html>