[#ftl]
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Enonic CMS - Authentication</title>
</head>
<body>

[#if loginFailed = true]
    <p style="font-weight: bold">
        Username or password is wrong
    </p>
[/#if]

<div>
    <form action="." method="post">
        <div>
            <input type="hidden" name="_xtrace_authentication" value="true"/>

            <!-- TODO: Add userstore dropdown -->

            <label for="username">Username:</label>
            <br/>
            <input type="text" name="_xtrace_username" id="username"/>
            <br/>
            <label for="password">Password:</label>
            <br/>
            <input type="password" name="_xtrace_password" id="password"/>
            <br/>
            <input type="submit" value="Log in"/>
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