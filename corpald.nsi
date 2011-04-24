; corpald.nsi
			;
			; GENERATED from ANT script - edit in build.xml, NOT in corpald.nsi!
			;

			;--------------------------------

			; The name of the installer
			name "IcePaHC 0.4 - The Icelandic Parsed Historical Corpus"

			; The file to write
			outFile "release/icepahc-0.4-windows-setup.exe"

			; The default installation directory
			installDir $PROGRAMFILES\icepahc-0.4

			; The text to prompt the user to enter a directory
			dirText "This will install IcePaHC on your computer. Choose a directory"

			;--------------------------------

			; The stuff to install
			Section "" ;No components page, name is not important

			; Set output path to the installation directory.
			setOutPath $INSTDIR

			; Put files there
			File /r /x *.icns release/windows/*
			;# create the uninstaller
			writeUninstaller "$INSTDIR\uninstall.exe"

			createDirectory "$SMPROGRAMS\icepahc 0.4"
			createShortCut "$SMPROGRAMS\icepahc 0.4\icepahc 0.4.lnk" "$INSTDIR\icepahc-0.4-corpald.exe"
			createShortCut "$SMPROGRAMS\icepahc 0.4\Uninstall icepahc 0.4.lnk" "$INSTDIR\uninstall.exe"


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
		