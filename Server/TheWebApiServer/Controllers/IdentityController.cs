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
using ForgotPasswordRequest = TheWebApiServer.Requests.ForgotPasswordRequest;
using TheWebApiServer.IServices;
using TheWebApiServer.Data;
using TheWebApiServer.Services;
using static System.Net.Mime.MediaTypeNames;
using Microsoft.VisualStudio.Web.CodeGenerators.Mvc.Templates.BlazorIdentity.Pages.Manage;


namespace TheWebApiServer.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class IdentityController : ControllerBase
    {
        private static readonly EmailAddressAttribute _emailAddressAttribute = new();

        private readonly UserManager<IdentityUser> _userManager;
        private readonly SignInManager<IdentityUser> _signInManager;
        private readonly IEmailSender _emailSender;
        private readonly IConfiguration _configuration;
        private readonly DataContext _context;
        private readonly VerificationCodeService _verificationCode;
        public IdentityController(
            UserManager<IdentityUser> userManager,
            SignInManager<IdentityUser> signInManager,
            IEmailSender emailSender,
            IConfiguration configuration,
            DataContext context,
            VerificationCodeService verificationCode)
        {
            _userManager = userManager;
            _signInManager = signInManager;
            _emailSender = emailSender;
            _configuration = configuration;
            _context = context;
            _verificationCode = verificationCode;
        }

        [HttpPost("Register")]
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


        [HttpPost("Login")]
        public async Task<ActionResult<string>> Login([FromBody] LoginRequest login)
        {
            var user = await _userManager.FindByNameAsync(login.Email);

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


        [HttpPost("ForgotPassword")]
        public async Task<IActionResult> ForgotPassword([FromBody] ForgotPasswordRequest model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            //to do
            long code = _verificationCode.GenerateCode(model.Email);
            await _emailSender.SendPasswordResetCodeAsync(model.Email, code.ToString());

           
            return Ok();
        }
        [HttpPost("ResetPassword")]
        public async Task<IActionResult> ResetPassword([FromBody] Requests.ResetPasswordRequest model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            //to do
            bool verficationRes=_verificationCode.VerficateCode(model.Email, long.Parse(model.Code));
            if (verficationRes)
            {
                var user = await _userManager.FindByEmailAsync(model.Email);
                string code=await _userManager.GeneratePasswordResetTokenAsync(user);
                var result = await _userManager.ResetPasswordAsync(user, code, model.NewPassword);
                if (result.Succeeded)
                {
                    return Ok();
                }
                else
                {
                    BadRequest("error");
                }
            }
            else
            {
                return BadRequest("incorrect values");
            }
            return BadRequest("incorrect values");
        }

        [HttpPost]
        [Route("ChangePassword")]
       /* [Authorize]*/
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            Console.WriteLine(userId);
            if (userId == null)
            {
                return NotFound("Nie można znaleźć użytkownika.");
            }
            var user=await _userManager.FindByIdAsync(userId);
            var result = await _userManager.ChangePasswordAsync(user, model.CurrentPassword, model.NewPassword);

            if (result.Succeeded)
            {
                return Ok("Hasło zostało pomyślnie zmienione.");
            }
            else
            {
                return BadRequest("Nie udało się zmienić hasła. Sprawdź poprawność bieżącego hasła i format nowego hasła.");
            }
        }


        [HttpPost]
        [Route("ChangeEmail")]
        /*[Authorize]*/
        public async Task<IActionResult> ChangeEmail([FromBody] ChangeEmailRequest model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);

            }
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            
           
            var user = await _userManager.FindByIdAsync(userId);

            if (user == null)
            {
                return NotFound("Nie można znaleźć użytkownika.");
            }

            user.Email = model.Email;
            user.UserName = model.Email;

            var result = await _userManager.UpdateAsync(user);

            if (result.Succeeded)
            {
                return Ok("Adres e-mail został pomyślnie zmieniony.");
            }
            else
            {
                return BadRequest("Nie udało się zmienić adresu e-mail. Sprawdź poprawność nowego adresu e-mail.");
            }
        }



    }
}

