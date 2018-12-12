<?php
// Authors:      Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
//  File: account.php
//  Description: This class is reponsible for handling post requests for creating new users, verifying their accounts
//	and changing a user's password.
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

class Account extends Controller {
	/**
	 * Constructor: Calls the parent constructor.
	 */
	public function __construct() {
		parent::__construct();
	}
	/**
	 * Index page.
	 */
	public function index() {
		// May not be needed.
	}
	/**
	* Creates a new user and add it to the database.
	*/
	public function create() {
		// Exit the function if a POST Request was not sent.
		if(!isset($_POST)) exit();
		// Exit the function if password and confirm passwords were different.
		if($_POST['password'] != $_POST['confirmpassword']) {
			set_title("Login Failed");
			$data = array('error' => "The passwords do not match.");
			$this->view('/home/newaccount', $data, $withIncludes = false);
			exit();
		}
		$account_creator = $this->model("AccountCreator");
		// Create a user model and set the provided information.
		$user = $this->model("User")
					 ->set_name($_POST['fullname'])
					 ->set_email($_POST['email'])
					 ->set_password($_POST['password']);
		$account_creator->create($user);
		// Only login and redirect to the dashboard if the user was created successfully.
		if($account_creator->did_create_an_account) {
			$_SESSION['current_user'] = $account_creator->new_account_id;
			$path = "/dashboard/index";
			header("Location: {$path}");	// Redirect the user to the dashboard page.
		} 
		// Otherwise display an error message.
		else {
			set_title("Login Failed");
			$data = array('error' => "A user already exists with this email.");
			$this->view('home/newaccount', $data, $withIncludes = false);
			exit();
		}
	}
	/**
	 * Password Change Success Page.
	 */
	public function password_reset_success() {
		$data = array();
		set_title("Password Changed");
		$this->view("account/changepassword_success", $data, $withIncludes = false);
	}
	/**
	 * View the request expired page.
	 */
	public function expired_request() {
		$data = array();
		$this->view("account/expired", $data, $withIncludes=false);
		exit();
	}
	
	/*
	 * Verifies the account with request_id
	 */
	public function verify($request_id="") {
		if($request_id == "") {
			$path = "/_404";
			header("Location: {$path}");	// Redirect the user to the dashboard page.
			exit();
		}
		
		$account_verifyer = $this->model("AccountVerifier");
		$account_verifyer->verify($request_id);
		if($account_verifyer->account_was_verified) {
			$account_verifyer->delete_request($request_id);
			$this->view("account/verify_account_success", [], $withIncludes=false);
		}
	}
	/**
	 * Allow the user to change his/her password with an email notification.
	 * You can only access this page via an email that was sent to the user.
	 */
	public function changepassword($request_id = "", $error = "") {
		if($request_id == "") {
			$path = "/_404";
			header("Location: {$path}");	// Redirect the user to the dashboard page.
			exit();
		}
		$password_request_handler = $this->model("PasswordRequestHandler");
		if(!$password_request_handler->is_legit_request($request_id)) {
			$this->expired_request();
		}
		if(!isset($_POST['submit'])) {
			$data = array("request_id" => $request_id);
			$warning = "";
			if($error == "nocaptcha") {
				$warning = "text-danger";
			}
			$data["message"] = "<p class='{$warning}'>Please prove you are human.</p>"; 
			set_title("Password Recovery");
			$this->view("account/changepassword", $data, $withIncludes = false);
			exit();
		}
		
		// Captcha
		$secret = "6LdJiXoUAAAAAHM10TyljNKeBDDnYzU5_Uz-EjhU";
		$response = $_POST["g-recaptcha-response"];
		$remoteip = $_SERVER["REMOTE_ADDR"];
		$url = "https://www.google.com/recaptcha/api/siteverify?secret={$secret}&response={$response}&remoteip={$remoteip}";
		$google = file_get_contents($url);
		$captcha = json_decode($google);
		
		$newpassword = $_POST['newpassword'];
		$newpassword_confirm = $_POST['newpassword_confirm'];
		
		if($newpassword != $newpassword_confirm) {
			echo "Passwords Must be the same.";
			exit();
		}	
		
		if(!$captcha->success) {
			$path = "/account/changepassword/{$request_id}/nocaptcha";
			header("Location: {$path}");	// Redirect if CAPTCHA v2 was not filled.
			exit();
		}
		
		$user_loader = $this->model("UserLoader");
		$user = $user_loader->fetch_with_reset_password_request($request_id);
		$password_changer = $this->model("PasswordChanger");
		$password_changer->change($user, $newpassword);
		if($password_changer->password_was_changed) {
			$this->password_reset_success();
			$password_request_handler->delete_request($request_id);
		}
		else echo "Password was not changed, please contact system admin.";
	}
}