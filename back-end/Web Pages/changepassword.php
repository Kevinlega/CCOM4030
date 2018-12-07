<style type="text/css">
	body {
		background-color: #112b63;
	}
</style>

<div class="container text-white">
	<h1 class="my-4 text-white text-center">Password Recovery</h1>
	<form action="/account/changepassword/<?php echo $data['request_id']; ?>" method="post">
	  <div class="form-row">
	  	<div class="form-group col">
	  		<label for="newpassword">New Password</label>
	    		<input id="newpassword" type="password" name="newpassword" class="form-control" placeholder="New Password" required>
	  	</div>
	  </div>
	  <div class="form-group form-row">
	    <div class="col">
	    	<label for="newpassword_confirm">Confirm Password</label>
	    	<input id="newpassword_confirm" type="password" name = "newpassword_confirm" class="form-control" placeholder="Confirm Password" required>
	    </div>
	  </div>
	  <div style="padding-bottom: 10px;" class="g-recaptcha" data-sitekey="6LdJiXoUAAAAAJPjZw3t9C01OY3mcx6dlHzrY_OD"></div>
	  <p><?= $data['message']; ?></p>
	  <button type="submit" name="submit" value="submit" class="btn btn-success form-control">Reset Password</button>
	</form>
</div>
