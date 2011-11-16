[#ftl]

[#function elipsis text len]
    [#if (text?length <= len-1)]
        [#return text]
    [/#if]

    [#return text?substring(0, len) + '...']
[/#function]

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Enonic CMS - Boot Page</title>

    <link rel="shortcut icon" type="image/x-icon" href="${baseUrl}/resources/favicon.ico"/>
    <link rel="stylesheet" href="${baseUrl}/resources/bootpage.css" type="text/css"/>
</head>

<body id="main">

<div id="logo">
    <a href="http://www.enonic.com" rel="external">
        <img alt="Enonic-logo" id="logo-screen" src="${baseUrl}/resources/images/logo-screen.gif" title="Enonic"/>
    </a>

    <ul class="menu horizontal main">
        <li><a title="Community" href="http://www.enonic.com/en/community" rel="external">Community</a></li>
        <li><a title="Documentation" href="http://www.enonic.com/en/docs" rel="external">Documentation</a></li>
        <li><a title="Support" href="http://www.enonic.com/en/support" rel="external">Support</a></li>
        <li><a title="Contact us" class=" last" href="http://www.enonic.com/en/contact-us" rel="external">Contact Enonic</a></li>
    </ul>

</div>

<div id="content-outer" class="clearfix">
    [#if modelUpgradeNeeded == true]
        <div class="error">
            <strong>Upgrade Needed!</strong>
            <br/>
            Database upgrade from model <b>${upgradeFrom}</b> to model <b>${upgradeTo}</b> is needed. Admin or site will not
            work correctly if not upgraded. Go to <a href="${baseUrl}/upgrade">upgrade</a> to upgrade.
        </div>
    [/#if]
    [#if softwareUpgradeNeeded == true]
        <div class="error">
            <strong>Software Upgrade Needed!</strong>
            <br/>
            Database model is newer than software allows. Please upgrade the software. Admin or site will not
            work correctly if not upgraded.
        </div>
    [/#if]

    [#assign adminUrl = baseUrl + '/admin' ]
    [#assign webDavUrl = baseUrl + '/dav']
    [#assign componentsUrlMaxLength = 30]

    <div id="content" class="clearfix">
        <div id="management-components" class="clearfix">
            <a class="component box admin left" title="${adminUrl}" href="${adminUrl}">
                <span class="icon-admin left">
                    <img src="${baseUrl}/resources/images/icon-admin.png" alt="Admin"/>
                </span>
                <span class="info left">
                    <span class="name">Admin Console</span>

                    <span>
                        ${elipsis(adminUrl, componentsUrlMaxLength)}
                    </span>
                </span>
            </a>

            <a class="component box left" title="${webDavUrl}" href="${webDavUrl}">
                <span class="icon-webdav left">
                    <img src="${baseUrl}/resources/images/folder-remote.png" alt="WebDAV"/>
                </span>
                <span class="info left">
                    <span class="name">WebDAV</span>

                    <span>
                        ${elipsis(webDavUrl, componentsUrlMaxLength)}
                    </span>
                </span>
            </a>
        </div>

        <div id="welcome">
            <h1>Welcome to Enonic CMS</h1>

            <p>
                Access this installation by choosing <strong>Admin Console</strong> above, or one of the
                <strong>sites</strong> to the right.<br/>
                <em><strong>Note:</strong> Default username/password for full access is: admin/password</em>
            </p>
        </div>

        <div id="steps">
            <div class="step clearfix">
                <div class="number left">1</div>
                <div class="description left">
                    <h2>Learn</h2>

                    <p>
                        Dig in to documentation for Editors, Administrators, Developers and Operators.<br/>
                        We also recommend developers to check out our tutorials.
                    </p>
                    <ul>
                        <li>
                            <a href="http://enonic.com/docs" rel="external">http://enonic.com/docs</a><br/>
                        </li>
                        <li>
                            <a href="http://enonic.com/tutorials" rel="external">http://enonic.com/tutorials</a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="step clearfix">
                <div class="number left">2</div>
                <div class="description left">
                    <h2>Create</h2>

                    <p>
                        Enonic provides sample templates, including a themes framework, utilities and modules for
                        building
                        new
                        sites
                        quickly.
                    </p>
                    <ul>
                        <li>
                            <a href="http://github.com/enonic/cms-packages" rel="external">http://github.com/enonic/cms-packages</a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="step last clearfix">
                <div class="number left">3</div>
                <div class="description left">
                    <h2>Share</h2>

                    <p>
                        Join the Enonic Community for Updates, Forum and Tutorials. Get help,
                        <br/>Discuss and share. All out
                        code is also available on GitHub - you are welcome with your contributions.<br/>
                    </p>
                    <ul>
                        <li>
                            <a href="http://enonic.com/community" rel="external">http://enonic.com/community</a><br/>
                        </li>
                        <li>
                            <a href="http://github.com/enonic" rel="external">http://github.com/enonic</a>
                        </li>
                    </ul>
                </div>
            </div>
            <div style="background: #ffffcc">
                <strong>Beta: </strong><a href="https://addons.mozilla.org/en-US/firefox/addon/enonic-cms-server-trace/" rel="external">Enonic CMS Server Trace</a> for Firebug
            </div>
        </div>
    </div>
    <div id="east">
        <div id="sites" class="box">
            <h2>Sites</h2>
            <ul>

            [#if upgradeNeeded == false]
                [#list sites?keys?sort as key]
                    [#assign url = baseUrl + '/site/' + sites[key] + '/']
                    <li>
                        <a href="${url}" title="${url}">
                            <span class="name">${key}</span>
                            <br/>
                            <span>
                                ${elipsis(url, 40)}
                            </span>
                        </a>
                    </li>
                [/#list]
            [/#if]
            [#if upgradeNeeded == true]
                <li>
                    <h3>N/A</h3>
                </li>
            [/#if]
            </ul>
        </div>
    </div>
</div>

<div class="clearfix" id="footer">
    <div id="license" class="left">
        ${versionTitleVersion} - Licensed under <a href="http://www.gnu.org/licenses/agpl.html" rel="external">AGPL 3.0</a>
    </div>
    <div id="social" class="right">
        <a href="http://www.enonic.com/en/rss" rel="external">
            <img src="${baseUrl}/resources/images/icon-rss-large.png" alt="RSS"/>
        </a>
        <a href="http://twitter.com/#!/enonic_cms" rel="external">
            <img src="${baseUrl}/resources/images/icon-twitter-large.png" alt="Enonic on Twitter"/>
        </a>
    </div>
</div>

<script type="text/javascript" src="${baseUrl}/resources/bootpage.js">//</script>

</body>
</html>
