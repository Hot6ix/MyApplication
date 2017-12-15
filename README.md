# MyApplication
A simple application that scan wifi and connect better wifi automatically

When you execute app, it requests permission for scan and change wifi.

Steps
1. Scanning
  Scan all available wifi around device.
2. Filtering
  Get configured wifi and if scanned wifi is already configured, add to list
3. Sorting
  Sort by level (Signal strength)
4. Comparing
  Compare connected wifi and list one by one
5. Connect and Check
  If connected wifi is better than others in list, do nothing
  If anything in list is better than connected wifi, change to the better wifi
  After change, it check that wifi is available and can connect to the internet
