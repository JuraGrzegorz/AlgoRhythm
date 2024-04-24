using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using TheWebApiServer.Data;
using TheWebApiServer.IServices;
using TheWebApiServer.Requests;
using TheWebApiServer.Services;

namespace TheWebApiServer.Controllers
{
    [ApiController]
    [Route("[Controller]")]
    public class MusicController : ControllerBase
    {
        private readonly IHubContext<MusicStreamingHub, IMusicStreamingHub> _hubContext;
        private readonly DataContext _context;
        public MusicController(IHubContext<MusicStreamingHub, IMusicStreamingHub> hubContext,DataContext context)
        {
            _hubContext = hubContext;
            _context = context;
        }

        [HttpGet("GetMusic")]
        [Authorize]
        public async Task GetMusic([FromBody]GetMusicRequest getMusicRequest)
        {
            
        }

        [HttpGet("GetMusicDataStream")]
        [Authorize]
        public async Task GetMusicStream([FromBody] GetMusicRequest getMusicRequest)
        {
            /*var filePath = "Data/pcm.pcm";

            using (var stream = new FileStream(filePath, FileMode.Open))
            {
                var buffer = new byte[256];
                int bytesRead;
                int i = 0;
                while ((bytesRead = await stream.ReadAsync(buffer, 0, buffer.Length)) > 0)
                {
                    await Clients.All.GetMusicBytes(buffer);
                }
            }*/
        }



    }
}
