A SpectX v1.4.83 patch and license generator

## Patching Procedure
- SpectX v1.4.83 Desktop should be installed.
- Locate `lib/spectx.jar` in the installation directory and place the `com` folder and the `patch.bat` file next to it:<br>
  ```
  lib/
  ├─ spectx.jar
  ├─ patch.bat
  └─ com/
     └─ spectx/
        └─ Ak.class
  ```
  
- Run `./patch.bat`
- Generate and apply a license.

## The Patch

This is how the patch works:

- Removes `CODESIGN.RSA` and `CODESIGN.SF` (PKCS7 signature, SHA-256 + RSA) files from `META-INF` to disable Java Runtime JAR verification. There is no need to delete the digests from `MANIFEST.MF`.
- Updates the JAR's `Ak.class` file. The modified class file contains:
  - Bypass JAR integrity verification.
  - Bypass License signature verifications (the two of them).

## License file
The license is wrapped in a PEM-like format: a file containing a Base64 ASCII encoding, with plain-text header and footer:
```
-----BEGIN SPECTX LICENSE-----
U1hsA...
                 ...
                             ...==
-----END SPECTX LICENSE-----
```
Note that, although the license is in a PEM-like format, its content has nothing to do with X.509 certificates. Any attempt to read it as a PEM certificate file will fail.

### SpectX license file specification

Here's the license file format specifications:

```
spectx.lic
├─ [ASCII Header]       PEM-like header.
|
├─ [ASCII Base64 Body]  ASCII Base64 encoded with variable lenght. Might use padding (=).
|  |
|  ├─ [Magic Number]    Fixed (4 bytes): 0x53, 0x58, 0x6C, 0x01. Reads SXl.
|  |
|  └─ [Deflated]        DEFLATE-compressed data. Any compression level is valid. ZLIB header and
|     |                 checksum fields are not used in order to support the compression format used
|     |                 in both GZIP and PKZIP.
|     |
|     ├─ [Signature]    SHA-256 + RSA Payload signature (256 bytes).
|     |
|     └─ [Payload]      java.util.Properties keys and values. Uses the ISO 8859-1 character encoding.
|                       Characters not in Latin-1 in the comments are written as \uxxxx for their
|                       appropriate unicode hexadecimal value xxxx. Characters less than \u0020 and
|                       characters greater than \u007E in property keys or values are written as
|                       \uxxxx for the appropriate hexadecimal value xxxx.
|
└─ [ASCII Footer]       PEM-like footer.
```

Both ASCII PEM-formatted header and footer are optional. When parsing a license string, any line beginning with a `-` character is discarted.
Note that the length of the license is variable, as it depends on the number and length of the properties in the payload and the compression ratio.

### License properties

Here's an **incomplete** list of valid properties that might be provided in a license.

| Property (Key) | Type | Required | Value in Free license | Description |
|---|---|---|---|---|
| License Type.<br>`type` | String | yes | `SpectX Free license` | If set to `SpectX AWS license` or `SpectX AWS Marketplace license` has a specific behabiour |
| Licensee email<br>`email` | String | yes | | Licensee email |
| License ID<br>`uuid` | UUID String | yes | | License ID |
| License expiry date<br>`expires` | RFC 2822 Date String | yes | | Must be a valid date.<br>Ex.: `Mon, 1 Aug 2112 00:00:00 GMT` |
| Name<br>`name` | String | no | | Probably used in Enterprise and Business licenses |
| Company<br>`company` | String | no | | Probably used in Enterprise and Business licenses |
| Max PU Count<br>`max.pu.count` | Numeric String | no | `2` | Max number of processing units allowed to be used<br>`0` means unlimited. |
| Max Users Count<br>`max.users.count` | Numeric String | no | | Max number of user accounts.<br>"0" means unlimited. |
| API Rate Limit (Value)<br>`api.ratelimit.value` | Numeric String | no | `100` | Limit for API request rate.<br>Ex.: `300`. |
| API Rate Limit (Time unit)<br>`api.ratelimit.timeunit` | Time String | no | `24h` | Limit for API request rate.<br>Ex.: `30s`. It can also be provided without unit character, which will then be interpreted as milliseconds. |
| Remote Access | | no | | If disabled, then both Web UI and API endpoints are allowed to be set up to listen only on http://localhost:8388 |
| Audit Logging<br>`audit.log.enabled` | String | no | `false` | Tells if audit logging is enabled |
| DA Protocol Access Configuration<br>`da.proto.access.configurable` | String | no | `false` | If disabled, an access to all data access protocols is unmanaged. If enabled, then the access is configurable for each protocol |
| DA ACL<br>`da.acl.configurable` | String | no | `false` | Tells if datastore rACL are configurable |
| JDBC Access Configuration<br>`jdbc.access.configurable` | String | no | `false` | If disabled, all JDBC drivers are allowed to be used. If enabled, JDBC drivers list is configurable |
| Elastic Access Configuration<br>`elastic.access.configurable` | String | no | `false` | |
| Resource ACL<br>`resource.acl.configurable` | String | no | `false` | Tells if resource ACL are configurable or not |
| Authentication<br>`webui.authentication.enabled` | String | no | `false` | Tells if user authentication in Web UI and API can be enabled or not |
| Local Group Management<br>`groups.local.enabled` | String | no | `false` | Tells if local user groups are supported or not|
| OpenId Connect Authentication<br>`webui.authentication.openid.enabled` | String | no | `false` | Tells if OpenId Connect authentication is allowed to be configured for Web UI or not |
| SAML Authentication<br>`webui.authentication.saml.enabled` | String | no | `false` | Tells if SAML authentication is allowed to be configured for Web UI or not |
| IWA Authentication<br>`webui.authentication.iwa.enabled` | String | no | `false` | Tells if IWA authentication is allowed to be configured for Web UI or not |
| Google Authentication<br>`webui.authentication.google.enabled` | String | no | `false` | Tells if Google authentication is allowed to be configured for Web UI or not |
| Pass-through Authentication<br>`webui.authentication.pt.enabled` | String | no | `false` | Tells if Pass-through authentication is allowed to be configured for Web UI or not |
| External Groups Provisioning<br>`groups.ext.enabled` | String | no | `false` | Tells if external user groups are supported or not |
| Google Workspace Log Fetch<br>`extapi.google.workspace.enabled` | String | no | `false` | Tells if Google Workspace logs fetching via Google Workspace Admin SDK Reports API is supported or not |
| Microsoft Azure Log Fetch<br>`extapi.microsoft.office365.enabled` | String | no | `false` | Tells if Microsoft Azure logs fetching via Microsoft Azure Monitor API and Microsoft Azure Active Directory Activity API is supported or not |
| Microsoft Office 365 Log Fetch<br>`extapi.microsoft.azure.enabled` | String | no | `false` | Tells if Microsoft Office 365 logs fetching via Office 365 Management Activity API is supported or not |
| Results Local Export<br>`export.results.local.enabled` | String | no | `false` | Tells if export of query results to local files is supported or not |
| Results Remote Export<br>`export.results.remote.enabled` | String | no | `false` | Tells if export of query results through upload to external URIs is supported or not |
| `additional_terms` | | no | | |
| `aws.account` | | no | | |
| `aws.instance` | | no | | |
| `aws.products` | | no | | |
| `acl.supported` | | no | | |

## Java License library

### Properties
The `com.sxlic.SequentialProperties` class represents the properties that can be included in a license.

```java
public void put(String key, Object value)
public void print()
```

Use `put()` to add new properties to the `SequentialProperties` object.

`print()` outputs in a human-readable the list of the properties in the `SequentialProperties` object.

### License

The `com.sxlic.License` class contains two methods for reading existing licenses and generating new ones. 

```java
public static String generate(SequentialProperties sprop)
public static SequentialProperties read(String license)
```

Using `generate()` returns a format compliant license PEM string. In order to apply a generated license to SpectX the JAR must be patched, since it contains an invalid signature (256 null bytes).

`read()` returns a valid `SequentialProperties` object containing the properties of the provided license PEM string.

### Example

Here's an example:

```java
// Craft new license properties
SequentialProperties licProp = new SequentialProperties();

licProp.put("type", "SpectX Free license");
licProp.put("email", "user@spectx.com");
licProp.put("uuid", UUID.randomUUID().toString());
licProp.put("expires", "Mon, 1 Aug 2112 00:00:00 GMT");
licProp.put("max.pu.count", "2");
 ...

// Generate new license from properties
String licPEM = License.generate(licProp);
System.out.println(licPEM);

// Read new license
SequentialProperties licPropRead = License.read(licPEM);
licPropRead.print();
```