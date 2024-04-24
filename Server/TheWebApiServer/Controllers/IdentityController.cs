using System;
using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Authentication.BearerToken;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using TheWebApiServer.Requests;
using LoginRequest = TheWebApiServer.Requests.LoginRequest;


namespace TheWebApiServer.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class IdentityController : ControllerBase
    {
        private static readonly EmailAddressAttribute _emailAddressAttribute = new();

        private readonly UserManager<IdentityUser> _userManager;
        private readonly SignInManager<IdentityUser> _signInManager;
        private readonly IEmailSender<IdentityUser> _emailSender;
        private readonly IConfiguration _configuration;
        public IdentityController(
            UserManager<IdentityUser> userManager,
            SignInManager<IdentityUser> signInManager,
            IEmailSender<IdentityUser> emailSender,
            IConfiguration configuration)
        {
            _userManager = userManager;
            _signInManager = signInManager;
            _emailSender = emailSender;
            _configuration = configuration;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterRequest registration)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var email = registration.Email;

            if (string.IsNullOrEmpty(email) || !_emailAddressAttribute.IsValid(email))
            {
                ModelState.AddModelError(nameof(registration.Email), "Invalid email format.");
                return BadRequest(ModelState);
            }

            var user = new IdentityUser { UserName = email, Email = email };
            var result = await _userManager.CreateAsync(user, registration.Password);

            if (!result.Succeeded)
            {
                foreach (var error in result.Errors)
                {
                    ModelState.AddModelError(string.Empty, error.Description);
                }

                return BadRequest(ModelState);
            }

           /* await _emailSender.SendEmailAsync(user, "Confirm your email",
                "Please confirm your email address by clicking this link.");
*/
            return Ok("User registered successfully.");
        }


        [HttpPost("login")]
        public async Task<ActionResult<string>> Login([FromBody] LoginRequest login)
        {
            var user = await _userManager.FindByNameAsync(login.Username);

            if (user != null && await _userManager.CheckPasswordAsync(user, login.Password))
            {
                var token = GenerateJwtToken(user.Id);
                return Ok(new { token });
            }

            return Unauthorized();
        }

        private string GenerateJwtToken(string UserId)
        {
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["JwtSettings:SecretKey"]));

            var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                issuer: _configuration["JwtSettings:Issuer"],
                audience: _configuration["JwtSettings:Audience"],
                claims: new[] { new Claim(ClaimTypes.NameIdentifier, UserId) },
                expires: DateTime.Now.AddDays(1),
                signingCredentials: credentials); 

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

       /* [HttpGet(Name = "GetJwt")]
        public string GetJwt()
        {
            return _configuration["JwtSettings:SecretKey"];
        }*/


        /*[HttpPost("logout")]
        [Authorize]
        public async Task<ActionResult> Logout()
        {
            await _signInManager.SignOutAsync().ConfigureAwait(false);
            return Ok("Wylogowano pomyślnie.");
        }*/
    }
}

