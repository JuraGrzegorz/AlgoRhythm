using System.Net.Mail;
using System.Net;
using TheWebApiServer.IServices;


namespace TheWebApiServer.Services
{
    public class EmailSender : IEmailSender
    {
        public async Task SendEmailAsync(string email, string subject, string htmlMessage)
        {
            var client = new SmtpClient("smtp.gmail.com", 587)
            {
                EnableSsl = true,
                Credentials = new NetworkCredential("juratestmail@gmail.com", "boyb iisg jfdm umek")
            };

            MailMessage message = new MailMessage("juratestmail@gmail.com", email, subject, htmlMessage);
            message.IsBodyHtml = true;

            await client.SendMailAsync(message);
        }
        public async Task SendPasswordResetCodeAsync(string email, string code)
        {
            await SendEmailAsync(email,"Password Reset", GetPasswordResetEmailTemplate(code));
        }
        private string GetPasswordResetEmailTemplate(string code)
        {
            return $@"
                <!DOCTYPE html>
                <html lang=""en"">
                <head>
                    <meta charset=""UTF-8"">
                    <meta http-equiv=""X-UA-Compatible"" content=""IE=edge"">
                    <meta name=""viewport"" content=""width=device-width, initial-scale=1.0"">
                    <title>Password Reset</title>
                    <style>
                        /* Stylizacja obramowania */
                        .container {{
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            padding: 20px;
                            max-width: 600px;
                            margin: 0 auto;
                            border-radius: 10px;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        }}
                        /* Stylizacja pojedynczej liczby */
                        .code-digit {{
                            padding: 5px;
                            margin: 5px;
                            border: 1px solid #ccc;
                            border-radius: 5px;
                        }}
                    </style>
                </head>
                <body>
                    <div class=""container"">
                        <h2 style=""color: #333;"">Resetowanie hasła</h2>
                        <p>{string.Join("", code.Select(c => char.IsDigit(c) ? $"<span class=\"code-digit\">{c}</span>" : c.ToString()))}</p>        
                    </div>
                </body>
                </html>";

        }
    }
}
