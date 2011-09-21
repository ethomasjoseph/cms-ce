[#ftl]
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Enonic CMS - Authentication</title>
</head>
<body>
[#if authenticationFailed = true]
    <p style="font-weight: bold">
        Username or password is wrong
    </p>
[/#if]

<div>
    <form action="." method="post">
        <div>
            <input type="hidden" name="_xtrace_authentication" value="true"/>
            <table>
                <tr>
                    <td>
                        <label for="userstore">Userstore:</label>
                    </td>
                    <td>
                        <select name="_xtrace_userstore" id="userstore">
                            [#list userStores?keys?sort as key]
                                <option value="${key}">${userStores[key]}</option>
                            [/#list]
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="username">Username:</label>
                    </td>
                    <td>
                        <input type="text" name="_xtrace_username" id="username"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="password">Password:</label>

                    </td>
                    <td>
                        <input type="password" name="_xtrace_password" id="password"/>
                    </td>
                </tr>
            </table>
            <br/>
                <input type="submit" value="Authenticate &gt;&gt;"/>
        </div>
    </form>
</div>

<script>
    document.getElementById( 'username' ).focus();
</script>

</body>
</html>
<html>
</html>