jQuery.sap.require("sap.com.employee.ImportemployeeImport.util.rester");
jQuery.sap.require("sap.ca.ui.message.message");
sap.ui.define([
	"jquery.sap.global",
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageToast",
	"sap/m/MessageBox",
	"sap/ui/model/Filter"
	], function (jQuery, Controller, JSONModel, MessageToast, MessageBox, Filter) {
	"use strict";

	var history = {
			prevPaymentSelect: null,
			prevDiffDeliverySelect: null
	};

	return Controller.extend("sap.com.employee.ImportemployeeImport.controller.V", {
		onInit: function () {
			this._wizard = this.byId("EmployeeWizard");
			this._oNavContainer = this.byId("wizardNavContainer");
			this._oWizardContentPage = this.byId("wizardContentPage");

			this.model = new sap.ui.model.json.JSONModel();
			this.postModel = new sap.ui.model.json.JSONModel();
			this.multiModel = new sap.ui.model.json.JSONModel();
			this.oModel = new sap.ui.model.json.JSONModel();
			this.usersModel = new sap.ui.model.json.JSONModel();
			this.model.attachRequestCompleted(null, function () {
				this.model.setProperty("/selectedMode", "Single");
				this.model.updateBindings();
			}.bind(this));

			this.model.loadData(jQuery.sap.getModulePath("sap.ui.demo.mock", "/model.json"));
			this.postModel.loadData(jQuery.sap.getModulePath("sap.ui.demo.mock", "/post.json"));
			this.multiModel.loadData(jQuery.sap.getModulePath("sap.ui.demo.mock", "/mEmployees.json"));
			this.oModel.setData({
				"Genders": [],
				"Languages": [],
				"CompanyCode": [],
				"CostCenter": []
			});

			this.getView().setModel(this.model);
			this.getView().setModel(this.postModel, "postM");
			this.getView().setModel(this.multiModel, "multiModel");
			this.getView().setModel(this.oModel, "oModel");
			this.getView().setModel(this.usersModel, "usersModel");
			this._readGenders();
			this._readLanguage();
			this._readCompanyCode();
			this._readUsers();
			this._onDialogInitialize();
		},

		_onDialogInitialize: function () {
			this._oDialog = sap.ui.xmlfragment("sap.com.employee.ImportemployeeImport.fragment.Dialog", this);
			// Multi-select if required
			this._oDialog.setMultiSelect(true);
			// Remember selections if required
			this._oDialog.setRememberSelections(true);
			this.getView().addDependent(this._oDialog);
		},

		_readGenders: function () {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/employee/gender",
				"callbackSuccess": function (data) {
					this.getView().getModel("oModel").setProperty("/Genders", data);
				}.bind(this),
				"callbackError": function (error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		_readLanguage: function () {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/employee/language",
				"callbackSuccess": function (data) {
					this.getView().getModel("oModel").setProperty("/Languages", data);
				}.bind(this),
				"callbackError": function (error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		_readCompanyCode: function () {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/employee/companycode",
				"callbackSuccess": function (data) {
					this.getView().getModel("oModel").setProperty("/CompanyCode", data);
				}.bind(this),
				"callbackError": function (error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		_readCostCenter: function (code, lang, sPath) {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/employee/costcenter?companyCode=" + code + "&language=" + lang,
				"callbackSuccess": function (data) {
					this.getView().getModel("multiModel").setProperty(sPath + "/modelCostCenter", data);
					this.getView().getModel("oModel").setProperty("/CostCenter", data);
				}.bind(this),
				"callbackError": function (error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		_readUsers: function () {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/business-user/all",
				"callbackSuccess": function (data) {
					this.getView().getModel("usersModel").setData(data);
				}.bind(this),
				"callbackError": function (error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		//Function to render the specific view, based on user's selection of employee creation mode
		goToEmployeeStep: function () {
			var selectedKey = this.model.getProperty("/selectedMode");
			if (selectedKey === "Single") {
				this.byId("EmployeeNumberStep").setNextStep(this.getView().byId("BasicEmployeeStep"));
				this._wizard.invalidateStep(this.byId("BasicEmployeeStep"));
			} else if (selectedKey === "Multiple") {
				this.byId("EmployeeNumberStep").setNextStep(this.getView().byId("BasicTableStep"));
				this._wizard.invalidateStep(this.byId("BasicTableStep"));
			}
		},

		//Function to show warning message when user tries to switch from one employee creation mode to other
		setEmployeeNumber: function () {
			this.setDiscardableProperty({
				message: "Are you sure you want to change the employee creation mode ? This will discard your progress.",
				discardStep: this.byId("EmployeeNumberStep"),
				modelPath: "/selectedMode",
				historyPath: "prevPaymentSelect"
			});
		},

		setDiscardableProperty: function (params) {
			if (this._wizard.getProgressStep() !== params.discardStep) {
				MessageBox.warning(params.message, {
					actions: [MessageBox.Action.YES, MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === MessageBox.Action.YES) {
							this._wizard.discardProgress(params.discardStep);
							history[params.historyPath] = this.model.getProperty(params.modelPath);
							this._refreshModel();
							this.multiModel.loadData(jQuery.sap.getModulePath("sap.ui.demo.mock", "/mEmployees.json"));
						} else {
							this.model.setProperty(params.modelPath, history[params.historyPath]);
						}
					}.bind(this)
				});
			} else {
				history[params.historyPath] = this.model.getProperty(params.modelPath);
			}
		},

		//Function to handle 'cancel' button selection in any of the two employee creation mode
		handleWizardCancel: function () {
			this._handleMessageBoxOpen("Are you sure you want to cancel ?", "warning");
		},

		_handleMessageBoxOpen: function (sMessage, sMessageBoxType) {
			MessageBox[sMessageBoxType](sMessage, {
				actions: [MessageBox.Action.YES, MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === MessageBox.Action.YES) {
						this._wizard.discardProgress(this._wizard.getSteps()[0]);
						this._navBackToList();
						this._refreshModel();
						this.multiModel.loadData(jQuery.sap.getModulePath("sap.ui.demo.mock", "/mEmployees.json"));
					}
				}.bind(this)
			});
		},

		//Function to navigate back to the first screen of the application
		_navBackToList: function () {
			this._navBackToStep(this.byId("ContentsStep"));
		},

		_navBackToStep: function (step) {
			var fnAfterNavigate = function () {
				this._wizard.goToStep(step);
				this._oNavContainer.detachAfterNavigate(fnAfterNavigate);
			}.bind(this);

			this._oNavContainer.attachAfterNavigate(fnAfterNavigate);
			this._oNavContainer.to(this._oWizardContentPage);
		},

		handleValidationWarning: function (oEvent) {
			oEvent.getSource().setValueState(sap.ui.core.ValueState.Error);
		},

		_refreshModel: function () {
			this.postModel.loadData(jQuery.sap.getModulePath("sap.ui.demo.mock", "/post.json"));
			this.byId("cmbBoxGender").clearSelection();
			this.byId("cmbBoxGender").setSelectedKey();
			this.byId("cmbBoxLang").clearSelection();
			this.byId("cmbBoxLang").setSelectedKey();
			this.byId("mainTable").removeSelections();
			sap.ui.getCore().byId("tableRoleDialog")._removeSelection();
		},

		//Function to handle submit in single employee creation mode
		onSubmit: function () {
			var payloadData = JSON.stringify(this.getView().getModel("postM").getData());
			this._showBusyIndicator();
			jQuery.proxy(sap.ui.demo.wt.util.rester.create({
				"data": payloadData,
				"url": "/employee",
				"callbackSuccess": function (response) {
					sap.m.MessageBox.show(
							"Employee/Business User created in S/4 HANA Cloud.", {
								icon: sap.m.MessageBox.Icon.SUCCESS,
								title: "Success",
								actions: [sap.m.MessageBox.Action.OK]
							}
					);
					this._hideBusyIndicator();
					this._refreshModel();

				}.bind(this),
				"callbackError": function (error) {
					this._hideBusyIndicator();
					sap.ca.ui.message.showMessageBox({
						type: sap.ca.ui.message.Type.ERROR,
						message: error.responseJSON.message
					});
				}.bind(this)
			}), this);
		},

		//For multiple employee creation mode, function for highlighting the erroneous row in red and showing the message box
		_showError: function (message) {
			var table = this.byId("mainTable");
			var newMultiModelInfo = this.multiModel.getData().Info;

			if (Array.isArray(message.responseJSON)) {
				var msg = "";
				message.responseJSON.forEach(function (message2, messageIndex) {
					newMultiModelInfo.forEach(function (item, itemIndex) {
						if (message.responseJSON[messageIndex].externalId === item.externalId) {
							var row = table.getItems()[itemIndex];
							row.addStyleClass("red");
						}
					});
					msg = msg + "\n" + message2.externalId + ": " + message2.message;
				});
				sap.ca.ui.message.showMessageBox({
					type: sap.ca.ui.message.Type.ERROR,
					message: msg
				});
			} else {
				newMultiModelInfo.forEach(function (item, itemIndex) {
					if (message.responseJSON.externalId === item.externalId) {
						var row = table.getItems()[itemIndex];
						row.addStyleClass("red");
						sap.ca.ui.message.showMessageBox({
							type: sap.ca.ui.message.Type.ERROR,
							message: message.responseJSON.message
						});
					}
				});
			}
		},

		//Function to add new row in multiple employee creation mode
		onNewRecordPress: function () {
			var M = this.multiModel.getData().Info;
			M.push({
				"externalId": null,
				"personalInformation": {
					"academicSecondTitle": null,
					"academicTitle": null,
					"additionalLastName": null,
					"birthName": null,
					"firstName": null,
					"formOfAddress": null,
					"gender": null,
					"initials": null,
					"language": null,
					"lastName": null,
					"lastNamePrefix": null,
					"lastNameSecondPrefix": null,
					"middleName": null,
					"nameSupplement": null,
					"nickName": null
				},
				"userName": null,
				"workInformation": {
					"building": null,
					"companyCode": null,
					"costCenter": null,
					"email": null,
					"phone": {
						"countryDialingCode": null,
						"phoneNumber": null,
						"phoneNumberAreaID": null,
						"phoneNumberExtension": null,
						"type": null
					},
					"roomNumber": null,
					"startDate": null,
					"endDate": null
				},
				"editable": true,
				"roles": []
			});
			this.multiModel.updateBindings();
		},

		//Function to handle submit in multiple employee creation mode
		onMultiSubmit: function () {
			var aInfoData = this.getView().getModel("multiModel").getData().Info;
			var payloadData = [];
			aInfoData.forEach(function (rowItem, rowIndex) {
				// iteration of object properties
				var newObject = {};
				if (rowItem.roles) {
					newObject.roles = rowItem.roles; 
				}
				if (rowItem.externalId) {
					newObject.externalId = rowItem.externalId;
				}
				newObject.personalInformation = {};
				if (rowItem.personalInformation.firstName) {
					newObject.personalInformation.firstName = rowItem.personalInformation.firstName;
				}
				if (rowItem.personalInformation.gender) {
					newObject.personalInformation.gender = rowItem.personalInformation.gender;
				}
				if (rowItem.personalInformation.language) {
					newObject.personalInformation.language = rowItem.personalInformation.language;
				}
				if (rowItem.personalInformation.lastName) {
					newObject.personalInformation.lastName = rowItem.personalInformation.lastName;
				}
				if (rowItem.userName) {
					newObject.userName = rowItem.userName;
				}
				newObject.workInformation = {};
				if (rowItem.workInformation.companyCode) {
					newObject.workInformation.companyCode = rowItem.workInformation.companyCode;
				}
				if (rowItem.workInformation.costCenter) {
					newObject.workInformation.costCenter = rowItem.workInformation.costCenter;
				}
				if (rowItem.workInformation.email) {
					newObject.workInformation.email = rowItem.workInformation.email;
				}
				if (rowItem.workInformation.phone.phoneNumber) {
					newObject.workInformation.phone.phoneNumber = rowItem.workInformation.phone.phoneNumber;
				}
				if (rowItem.workInformation.startDate) {
					newObject.workInformation.startDate = rowItem.workInformation.startDate;
				}
				//Push the newObject to payloadData, only if mandatory rowItems are not empty
				if (rowItem.editable && rowItem.externalId && rowItem.personalInformation.firstName && rowItem.personalInformation.lastName &&
						rowItem.userName && rowItem.personalInformation.gender && rowItem.workInformation.companyCode && rowItem.workInformation.startDate
				) {
					payloadData.push(newObject);
				}
			});
			//If payloadData is not empty, send a post request
			if (payloadData.length !== 0) {
				this._showBusyIndicator();
				jQuery.proxy(sap.ui.demo.wt.util.rester.create({
					"data": JSON.stringify(payloadData),
					"url": "/employee/batch",
					"callbackSuccess": function (response) {
						var newMultiModelInfo = this.multiModel.getData().Info;
						var table = this.byId("mainTable");
						response.forEach(function (message, messageIndex) {
							newMultiModelInfo.forEach(function (item, itemIndex) {
								if (message.externalId === item.externalId) {
									if (message.status === "OK") {
										//For 'OK' status, show a message toast
										sap.m.MessageToast.show(message.message, {
											duration: 3500
										});
										item.editable = false;
									} else {
										//For status except 'OK', highlight the row in red and show a message box
										var row = table.getItems()[itemIndex];
										row.addStyleClass("red");
										sap.ca.ui.message.showMessageBox({
											type: sap.ca.ui.message.Type.ERROR,
											message: message.message
										});
									}
								}
							});
						});
						this.multiModel.updateBindings();
						this._hideBusyIndicator();
					}.bind(this),
					"callbackError": function (error) {
						this._showError(error);
						this._hideBusyIndicator();
					}.bind(this)
				}), this);
			} else {
				var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
				//If payloadData is empty, show a message box with error
				MessageBox.error(
						"Please complete the required fields.", {
							styleClass: bCompact ? "sapUiSizeCompact" : ""
						}
				);
			}
		},

		//Function to validate data in single employee creation mode
		onInputChange: function (oEvent) {
			var ID = oEvent.getSource().getModel("postM").getData().externalId;
			var userN = oEvent.getSource().getModel("postM").getData().userName;
			var fn = oEvent.getSource().getModel("postM").getData().personalInformation.firstName;
			var ln = oEvent.getSource().getModel("postM").getData().personalInformation.lastName;
			var compCode = oEvent.getSource().getModel("postM").getData().workInformation.companyCode;
			var sDate = oEvent.getSource().getModel("postM").getData().workInformation.startDate;

			if ((!(ID === "") && !(userN === "") && !(fn === "") && !(ln === "") && !(compCode === "") && !(sDate === "")) && (ID !== null &&
					userN !== null && fn !== null && ln !== null && compCode !== null && sDate !== null)) {
				this.byId("sbmtButton").setEnabled(true);
			} else {
				this.byId("sbmtButton").setEnabled(false);
			}
		},

		//Populate Cost Center drop-down, based on the user selection of Company Code
		onCompCodeChange: function (oEvent) {
			var aCompCode = this.getView().getModel("oModel").getData().CompanyCode;
			var key = oEvent.getSource().getSelectedKey();
			var that = this;
			if (this.model.getData().selectedMode === "Multiple") {
				var sPath = oEvent.getSource().getBindingContext("multiModel").sPath;
			}
			jQuery.each(aCompCode, function (index, obj) {
				if (obj.code === key) {
					that._readCostCenter(key, obj.language, sPath);
					return false;
				}

			});
		},

		handleErrorMessageBoxPress: function (msg) {
			var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
			MessageBox.error(msg, {
				styleClass: bCompact ? "sapUiSizeCompact" : ""
			});
		},

		_showBusyIndicator: function () {
			// instantiate dialog
			if (!this._BusyDialog) {
				this._BusyDialog = sap.ui.xmlfragment("sap.com.employee.ImportemployeeImport.fragment.BusyDialog", this);
				this.getView().addDependent(this._dialog);
			}
			// open dialog
			jQuery.sap.syncStyleClass("sapUiSizeCompact", this.getView(), this._BusyDialog);
			this._BusyDialog.open();
		},

		_hideBusyIndicator: function () {
			this._BusyDialog.close();
		},

		/****************Copy Roles From User Dialog******************/

		handleCopyFromUser: function (oEvent) {
			if (!this._oDialogUser) {
				this._oDialogUser = sap.ui.xmlfragment("sap.com.employee.ImportemployeeImport.fragment.CopyFromUser", this);
			}
			this.getView().addDependent(this._oDialogUser);
			// toggle compact style
			jQuery.sap.syncStyleClass("sapUiSizeCompact", this.getView(), this._oDialogUser);
			this._oDialogUser.open();
		},

		handleConfirmSelect: function (oEvent) {
			var aContexts = oEvent.getParameter("selectedContexts");
			var oRolesModel = this.getView().getModel("postM").getData().roles;
			var oMultiModel = this.getView().getModel("multiModel").getData().Info;
			var oTbl = this.byId("mainTable");
			var aSelectedItems = oTbl.getSelectedItems();
			var aTableRoleItems = sap.ui.getCore().byId("tableRoleDialog").getItems();

			if (aContexts && aContexts.length) {
				aContexts.map(function (oContext) {
					var oRoles = oContext.getObject().user.role;
					jQuery.each(oRoles, function (index, item) {
						return oRolesModel.push(item.roleName);
					});
				});
			}

			oRolesModel.forEach(function (item2) {
				aTableRoleItems.find(function (item1) {
					if (item1.getCells()[1].getText() === item2) {
						item1.setSelected(true);
					}
				});
			});

			if (aSelectedItems.length !== 0) {
				jQuery.each(aSelectedItems, function (index, item) {
					var path = item.getBindingContextPath();
					var pathSubstr = path.substr(6);
					var roles = oMultiModel[pathSubstr].roles;
					aContexts.map(function (oContext) {
						var oRoles = oContext.getObject().user.role;
						jQuery.each(oRoles, function (i, elm) {
							return roles.push(elm.roleName);
						});
					});
				});
			}

			oEvent.getSource().getBinding("items").filter([]);
		},

		handleSearchUser: function (oEvent) {
			var sValue = oEvent.getParameter("value");
			var oFilter = new Filter("user/userName", sap.ui.model.FilterOperator.Contains, sValue);
			var oBinding = oEvent.getSource().getBinding("items");
			oBinding.filter([oFilter]);
		},
		/**************** End Of Copy Roles From User Dialog******************/

		/******************MultiInput with tokens for roles******************/

		onSubmitRolesDialog: function (context) {
			var dialog = new sap.m.Dialog({
				title: "Roles",
				type: "Message",
				contentHeight: "200px",
				content: [
					new sap.m.Label({
						text: "",
						labelFor: "submitDialogTextarea"
					}),
					new sap.m.MultiInput("submitDialogTextarea", {
						liveChange: function (oEvent) {
							var sText = oEvent.getParameter("value");
							var parent = oEvent.getSource().getParent();

							parent.getBeginButton().setEnabled(sText.length > 0);
						},
						submit: function (oEvent) {
							oEvent.getSource().addValidator(function (args) {
								var text = args.text;

								return new sap.m.Token({
									key: text.toUpperCase(),
									text: text.toUpperCase()
								});
							});
						},
						width: "auto",
						enableMultiLineMode: true,
						placeholder: "Add roles by typing manually the name..",
						showValueHelp: false
					})
					],
					beginButton: new sap.m.Button({
						text: "Submit",
						enabled: false,
						press: function (oEvent) {
							this._onSubmitDialogButton();
							dialog.close();
						}.bind(this)
					}),
					endButton: new sap.m.Button({
						text: "Cancel",
						press: function () {
							dialog.close();
						}
					}),
					afterClose: function () {
						dialog.destroy();
					}
			});
			this.getView().addDependent(dialog);
			dialog.open();
		},

		_onSubmitDialogButton: function () {
			var aTokens = sap.ui.getCore().byId("submitDialogTextarea").getTokens();
			var aRolesModel = this.getView().getModel("postM").getData().roles;
			var mModel = this.getView().getModel("multiModel").getData().Info;
			var oTbl = this.byId("mainTable");
			var aSelectedItems = oTbl.getSelectedItems();

			aTokens.forEach(function (item) {
				aRolesModel.push(item.getKey());
			});

			if (aSelectedItems.length !== 0) {
				jQuery.each(aSelectedItems, function (index, item) {
					var path = item.getBindingContextPath();
					var pathSubstr = path.substr(6);
					var roles = mModel[pathSubstr].roles;
					aTokens.map(function (elm) {
						return roles.push(elm.getKey());
					});
				});
			}
		}
		
		/******************End Of MultiInput with tokens for roles******************/

	});
});
