; corpald.nsi
;
; This script is perhaps one of the simplest NSIs you can make. All of the
; optional settings are left to their default settings. The installer simply 
; prompts the user asking them where to install, and drops a copy of "MyProg.exe"
; there.

;--------------------------------

; The name of the installer
name "Corpald"

; The file to write
outFile "corpald-install.exe"

; The default installation directory
installDir $PROGRAMFILES\corpald

; The text to prompt the user to enter a directory
dirText "This will install Corpald on your computer. Choose a directory"

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

; Set output path to the installation directory.
setOutPath $INSTDIR

; Put file there
;File release/windows/icepahc-0-4-corpald.exe
File /r /x *.icns release/windows/*
;# create the uninstaller
writeUninstaller "$INSTDIR\uninstall.exe"

createDirectory "$SMPROGRAMS\Corpald"
createShortCut "$SMPROGRAMS\Corpald\Corpald.lnk" "$INSTDIR\icepahc-0-4-corpald.exe"
createShortCut "$SMPROGRAMS\Corpald\Uninstall Corpald.lnk" "$INSTDIR\uninstall.exe"


WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Corpald" "DisplayName"\
"Corpald (remove only)"

WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Corpald" "UninstallString" \
"$INSTDIR\uninstall.exe"

sectionEnd ; end the section

# uninstaller section start
section "uninstall"
 
    # first, delete the uninstaller
    delete "$INSTDIR\uninstall.exe"
 
    # second, remove the link from the start menu
    delete "$SMPROGRAMS\Corpald.lnk"

    RMDIR /r "$SMPROGRAMS\Corpald"

    # delete registry keys
    DeleteRegKey HKEY_LOCAL_MACHINE "Software\Corpald"
    DeleteRegKey HKEY_LOCAL_MACHINE "Software\Microsoft\Windows\CurrentVersion\Uninstall\Corpald"
sectionEnd
