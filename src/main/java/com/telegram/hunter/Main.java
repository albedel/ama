package com.telegram.hunter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.telegram.hunter.dto.KnifeDTO;
import com.telegram.hunter.dto.LoginDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TelegramClient;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
	
	public static Display display;
	public static Shell shell;
	
	private static final LoginDTO defaultLoginDTO = new LoginDTO();
	
	static {
		try {
			List<String> lines = FileUtil.readLines("C:/tdlib-db/login.txt", StandardCharsets.UTF_8);
			if (CollectionUtils.isNotEmpty(lines) && lines.size() >= 3) {
				defaultLoginDTO.setApiId(StringUtils.trim(lines.get(0)));
				defaultLoginDTO.setApiHash(StringUtils.trim(lines.get(1)));
				defaultLoginDTO.setPhone(StringUtils.trim(lines.get(2)));
			}
		} catch (IORuntimeException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		Main.display = display;
		Main.shell = shell;
		
		shell.setText("Robot");
		shell.setSize(1600, 880);
		shell.setLayout(new GridLayout(2, false));
		((GridLayout) shell.getLayout()).verticalSpacing = 15;
		
		// set screen size
		Rectangle screenSize = display.getBounds();
		Rectangle shellSize = shell.getBounds();
		int x = (screenSize.width - shellSize.width) / 2;
		int y = (screenSize.height - shellSize.height) / 2;
		shell.setLocation(x, y);
		
		CTabFolder tabFolder = new CTabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// add tab menu event
		tabFolder.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				for (CTabItem item : tabFolder.getItems()) {
					if (item == tabFolder.getSelection()) {
						Font boldFont = new Font(display, item.getFont().getFontData()[0].getName(), item.getFont().getFontData()[0].getHeight(), SWT.BOLD);
						item.setFont(boldFont);
					} else {
						Font normalFont = new Font(display, item.getFont().getFontData()[0].getName(), item.getFont().getFontData()[0].getHeight(), SWT.NORMAL);
						item.setFont(normalFont);
					}
				}
			}
		});
		
		// Telegram
		swtTelegram(display, shell, tabFolder);
		
		// knife
		swtKnife(display, shell, tabFolder);
		
		// log
        Text outputText = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		GridData outputData = new GridData(SWT.FILL, SWT.TOP, true, false);
		outputData.heightHint = 800;
		outputData.widthHint = 600;
		outputText.setLayoutData(outputData);
		outputText.setEditable(false);
		
		// add right click function clean
		Menu menu = new Menu(outputText);
		MenuItem clearItem = new MenuItem(menu, SWT.PUSH);
		clearItem.setText("clean");
		clearItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				outputText.setText("");
			}
		});
		outputText.setMenu(menu);
		
		// echo System.out.println
		asyncRedirectSystemOut(display, outputText);
		
        //shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
        System.exit(0);
	}
	
	/**
	 * knife
	 * @param display
	 * @param shell
	 * @param tabFolder
	 */
	private static void swtKnife(Display display, Shell shell, CTabFolder tabFolder) {
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setText(" knife ");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		tabItem.setControl(composite);
		
        // group
        Label groupLabel = new Label(composite, SWT.NONE);
        groupLabel.setText("group");
        groupLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        StyledText groupText = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData groupGridData = new GridData(GridData.FILL_HORIZONTAL);
        groupGridData.widthHint = 500;
        groupGridData.heightHint = 120;
        groupText.setLayoutData(groupGridData);
        
        // user
        Label userLabel = new Label(composite, SWT.NONE);
        userLabel.setText("user");
        userLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        StyledText userText = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData userGridData = new GridData(GridData.FILL_HORIZONTAL);
        userGridData.widthHint = 500;
        userGridData.heightHint = 120;
        userText.setLayoutData(userGridData);
        
        // dest
        Label destLabel = new Label(composite, SWT.NONE);
        destLabel.setText("dest");
        destLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        Text destText = new Text(composite, SWT.BORDER);
        destText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // interval
        Label intervalLabel = new Label(composite, SWT.NONE);
        intervalLabel.setText("interval(second)");
        intervalLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        Text intervalText = new Text(composite, SWT.BORDER);
        intervalText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        intervalText.setText("1");
        
        // scratch
        Button submitButton = new Button(composite, SWT.PUSH);
        submitButton.setText("start");
        GridData submitButtonData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        submitButtonData.horizontalSpan = 2;
        submitButtonData.heightHint = 45;
        submitButton.setLayoutData(submitButtonData);
        
        groupLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				InputDialog dialog = new InputDialog(Main.shell, "", "Please enter the group ID", "", null);
				if (dialog.open() == InputDialog.OK) {
					String groupId = dialog.getValue();
					if (StringUtils.isNotBlank(groupId)) {
		                TelegramClient.getChatAdministrators(groupId);
					}
				}
			}
		});
        
        userLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				InputDialog dialog = new InputDialog(Main.shell, "", "Please enter the userName", "", null);
				if (dialog.open() == InputDialog.OK) {
					String username = dialog.getValue();
					if (StringUtils.isNotBlank(username)) {
		                TelegramClient.getUserIdByUsername(username);
					}
				}
			}
		});
        
        submitButton.addListener(SWT.Selection, event -> {
        	String text = submitButton.getText();
        	switch (text) {
				case "start":
					// group
					List<String> groups = Lists.newArrayList();
					for (int i = 0; i < groupText.getLineCount(); i++) {
						String line = StringUtils.trim(groupText.getLine(i));
						if (StringUtils.isNotBlank(line)) {
							groups.add(line);
						}
					}
					// user
					List<String> users = Lists.newArrayList();
					for (int i = 0; i < userText.getLineCount(); i++) {
						String line = StringUtils.trim(userText.getLine(i));
						if (StringUtils.isNotBlank(line)) {
							users.add(line);
						}
					}
					
					KnifeDTO knifeDTO = new KnifeDTO();
					knifeDTO.setGroups(groups);
					knifeDTO.setUsers(users);
					knifeDTO.setDest(StringUtils.trim(destText.getText()));
					knifeDTO.setInterval(StringUtils.trim(intervalText.getText()));
					
					// check form
					if (!checkForm(shell, knifeDTO)) {
						return;
					}
					
					submitButton.setText("stop");
					new Thread(() -> {
						TelegramClient.startKnife = true;
						try {
							TelegramClient.startKnife(knifeDTO);
						} finally {
							display.asyncExec(() -> {
								submitButton.setText("start");
								TelegramClient.startKnife = false;
							});
						}
					}).start();
					break;
				case "stop":
					submitButton.setText("start");
					TelegramClient.startKnife = false;
					break;
				default:
					break;
			}
        });
	}
	
	/**
	 * check form
	 * @param shell
	 * @param knifeDTO
	 */
	private static boolean checkForm(Shell shell, KnifeDTO knifeDTO) {
		if (CollectionUtils.isEmpty(knifeDTO.getGroups())) {
			alert(shell, SWT.ICON_ERROR, "group ID can not be empty");
			return false;
		} else {
			if (knifeDTO.getGroups().size() > 10) {
				alert(shell, SWT.ICON_ERROR, "group num max limit 10");
				return false;
			}
		}
		
		if (StringUtils.isBlank(knifeDTO.getDest())) {
			alert(shell, SWT.ICON_ERROR, "dest can not be empty");
			return false;
		}
		
		if (StringUtils.isBlank(knifeDTO.getInterval())) {
			alert(shell, SWT.ICON_ERROR, "interval can not be empty");
			return false;
		} else {
			if (!NumberUtil.isInteger(knifeDTO.getInterval())) {
				alert(shell, SWT.ICON_ERROR, "interval format is incorrect");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Telegram
	 * @param display
	 * @param shell
	 * @param tabFolder
	 */
	private static void swtTelegram(Display display, Shell shell, CTabFolder tabFolder) {
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setText(" Telegram ");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		tabItem.setControl(composite);
		
        // apiId
        Label apiIdLabel = new Label(composite, SWT.NONE);
        apiIdLabel.setText("apiId");
        apiIdLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        Text apiIdText = new Text(composite, SWT.BORDER);
        GridData apiIdGridData = new GridData(GridData.FILL_HORIZONTAL);
        apiIdGridData.widthHint = 500;
        apiIdText.setLayoutData(apiIdGridData);
        apiIdText.setLayoutData(apiIdGridData);
        apiIdText.setText(StringUtils.defaultString(defaultLoginDTO.getApiId()));
        
        // apiHash
        Label apiHashLabel = new Label(composite, SWT.NONE);
        apiHashLabel.setText("apiHash");
        apiHashLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        Text apiHashText = new Text(composite, SWT.BORDER);
        apiHashText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        apiHashText.setText(StringUtils.defaultString(defaultLoginDTO.getApiHash()));
        
        // phone
        Label phoneLabel = new Label(composite, SWT.NONE);
        phoneLabel.setText("phone");
        phoneLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        Text phoneText = new Text(composite, SWT.BORDER);
        phoneText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        phoneText.setText(StringUtils.defaultString(defaultLoginDTO.getPhone()));
        
        // 复选框
        Composite checkboxContainer = new Composite(composite, SWT.NONE);
        checkboxContainer.setLayout(new GridLayout(5, false));
        GridData checkboxGridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        checkboxGridData.horizontalSpan = 2;
        checkboxContainer.setLayoutData(checkboxGridData);
        
        Button useProxyCheckbox = new Button(checkboxContainer, SWT.CHECK);
        useProxyCheckbox.setText("use proxy");
        Label blankLabel = new Label(checkboxContainer, SWT.NONE);
        blankLabel.setText("	");
        Button onlyCa = new Button(checkboxContainer, SWT.CHECK);
        onlyCa.setText("scratch ca");
        
        // Login
        Button submitButton = new Button(composite, SWT.PUSH);
        submitButton.setText("login");
        GridData submitButtonData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        submitButtonData.horizontalSpan = 2;
        submitButtonData.heightHint = 45;
        submitButton.setLayoutData(submitButtonData);
        
        useProxyCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	Client.useProxy = useProxyCheckbox.getSelection();
            }
        });
        
        onlyCa.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	TelegramClient.onlyCa = onlyCa.getSelection();
            }
        });
        
        submitButton.addListener(SWT.Selection, event -> {
        	String text = submitButton.getText();
        	switch (text) {
				case "login":
					LoginDTO loginDTO = new LoginDTO();
					loginDTO.setApiId(StringUtils.trim(apiIdText.getText()));
					loginDTO.setApiHash(StringUtils.trim(apiHashText.getText()));
					loginDTO.setPhone(StringUtils.trim(phoneText.getText()));
					
					if (!checkForm(shell, loginDTO)) {
						return;
					}
					
					submitButton.setText("Logging in…");
					
					new Thread(() -> {
						try {
							TelegramClient.startup(shell, Integer.parseInt(loginDTO.getApiId()), loginDTO.getApiHash(), loginDTO.getPhone());
					} finally {
							display.asyncExec(() -> {
								submitButton.setText("login success");
								submitButton.setEnabled(false);
							});
						}
					}).start();
					break;
				default:
					break;
			}
        });
	}
	
	/**
	 * check form
	 * @param shell
	 * @param loginDTO
	 */
	private static boolean checkForm(Shell shell, LoginDTO loginDTO) {
		if (StringUtils.isBlank(loginDTO.getApiId())) {
			alert(shell, SWT.ICON_ERROR, "apiId can not be empty");
			return false;
		}
		if (StringUtils.isBlank(loginDTO.getApiHash())) {
			alert(shell, SWT.ICON_ERROR, "apiHash can not be empty");
			return false;
		}
		if (StringUtils.isBlank(loginDTO.getPhone())) {
			alert(shell, SWT.ICON_ERROR, "phone can not be empty");
			return false;
		}
		return true;
	}
	
	/**
	 * alert
	 * @param shell
	 * @param type
	 * @param message
	 */
	private static void alert(Shell shell, int type, String message) {
		MessageBox messageBox = new MessageBox(shell, SWT.OK | type);
        messageBox.setMessage(message);
        messageBox.open();
	}
	
	/**
	 * System.out.println
	 * @param display
	 * @param outputText
	 */
	private static void asyncRedirectSystemOut(Display display, Text outputText) {
		new Thread(() -> redirectSystemOut(display, outputText)).start();
	}
	
	private static void redirectSystemOut(Display display, Text outputText) {
		PipedOutputStream pipedOutputStream = null;
		PipedInputStream pipedInputStream = null;
		try {
			pipedOutputStream = new PipedOutputStream();
			System.setOut(new PrintStream(pipedOutputStream, true));

			pipedInputStream = new PipedInputStream(pipedOutputStream);
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pipedInputStream))) {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					String str = line;
					display.asyncExec(() -> {
						if (!outputText.isDisposed()) {
							outputText.append(str + System.getProperty("line.separator"));
						}
					});
				}
			}
		} catch (IOException e) {
			redirectSystemOut(display, outputText);
		} finally {
			try {
				if (pipedOutputStream != null) {
					pipedOutputStream.close();
				}
				if (pipedInputStream != null) {
					pipedInputStream.close();
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
}
