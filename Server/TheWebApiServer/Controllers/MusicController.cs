using System;
using System.Runtime.CompilerServices;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using TheWebApiServer.Data;
using TheWebApiServer.IServices;
using TheWebApiServer.Models;
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
        /*[Authorize]*/
        public async Task<IActionResult> GetMusic([FromQuery]GetMusicRequest model)
        {
            var song = await _context.Songs
             .Where(x => x.Id == model.MusicId)
             .Select(x => new
             {
                 x.Id,
                 x.Title,
                 x.MusicLength,
                 x.ThumbnailData
             })
             .FirstOrDefaultAsync();
            if(song == null)
            {
                return BadRequest("invalid MusicId");
            }

             return Ok(song);
        }

        [HttpPost("GetMusicDataStream")]
        /*[Authorize]*/
        public async Task<IActionResult> GetMusicStream([FromBody] GetMusicStreamRequest model)
        {
            if (!ModelState.IsValid || model.SizeOfDataFrame==0)
            {
                if (model.SizeOfDataFrame == 0)
                    return BadRequest("SizeOfDataFrame can't be 0");
                return BadRequest(ModelState);
            }

            byte[] data = _context.Songs
                .Where(x => x.Id == model.MusicId)
                .Select(x => x.MusicData)
                .FirstOrDefault();

            if (data==null)
            {
                return BadRequest("inncorect Music Id");
            }
            if (model.MusicOffSet > data.Length)
                return BadRequest("MusicOffSet to Height");
            if (model.MusicOffSet + model.SizeOfMusicData > data.Length)
                model.SizeOfMusicData= data.Length-model.MusicOffSet;

            if (model.SizeOfMusicData < model.SizeOfDataFrame)
                model.SizeOfDataFrame = model.SizeOfMusicData;
            
            var buffer = new byte[model.SizeOfDataFrame];
            int len = 0;
            int index = model.MusicOffSet;
            while (len < model.SizeOfMusicData)
            {
                if(model.SizeOfMusicData<len+model.SizeOfDataFrame)
                    model.SizeOfDataFrame=model.SizeOfMusicData-len;

                Array.Copy(data,index,buffer,0,model.SizeOfDataFrame);
                index+=model.SizeOfDataFrame;
                len+= model.SizeOfDataFrame;
                /*await _hubContext.Clients.All.GetMusicBytes(buffer);*/
                await _hubContext.Clients.Client(model.SocketId).GetMusicBytes(buffer);
            }

            /*await _hubContext.Clients.Client(model.SocketId).GetMusicBytes(buffer);*/
            return Ok();
        }

        [HttpGet("GetProposedMusic")]
        public async Task<IActionResult> GetProposedMusic(int CountOfProposedMusic)
        {
            if (CountOfProposedMusic < 1)
            {
                return BadRequest("incorrect value of CountOfProposedMusic");
            }
            var songs = await _context.Songs
            .Select(x=> new
            {
                x.Id,
                x.Title,
                x.MusicLength,
                x.ArtistId,
                ArtistName=x.Artist.Name,
                x.ThumbnailData
            })
            .OrderBy(x => Guid.NewGuid())
            .Take(CountOfProposedMusic)
            .ToListAsync();
            return Ok(songs);
        }


        [HttpPost("LikeMusic")]
        [Authorize]
        public async Task<IActionResult> LikeMusic([FromQuery] int musicId)
        {
           
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
 
            var song = await _context.Songs.FindAsync(musicId);
            if (song == null)
            {
                return BadRequest("Taka muzyka nie istnieje");
            }

            _context.Favourites.Add(new Favourites
            {
                SongId = musicId,
                UserId = userId
            });

            await _context.SaveChangesAsync();
            return Ok();
        }


        /*[HttpGet("GetCoutOfConnetedUsers")]
        public async Task<IActionResult> GetCoutOfConnetedUsers()
        {
            return Ok(MusicStreamingHub.GetAllClients());
        }*/

        /*[HttpGet("testGetMusic")]
        [Authorize]
        public async Task<IActionResult> testGetMusic()
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
           *//* var res=_recommendationModel.GetRecommendationsForUser(userId,2);*//*
            return Ok();
        }*/

        [HttpGet("GetMusicByTitle")]
        public async Task<IActionResult> GetMusicByTitle([FromQuery] string titleSubString, int countOfReturnedMusic)
        {
            

            var containingTitle = await _context.Songs
                .Where(x => x.Title.ToLower().Contains(titleSubString.ToLower()))
                .Select(x => new
                {
                    x.Id,
                    x.Title,
                    x.MusicLength,
                    x.ArtistId,
                    ArtistName = x.Artist.Name,
                    x.ThumbnailData
                })
                .Take(countOfReturnedMusic)
                .ToListAsync();
            return Ok(containingTitle);
        }


    }
}
