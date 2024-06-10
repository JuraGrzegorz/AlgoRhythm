using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using Microsoft.AspNetCore.Mvc;
using TheWebApiServer.Services;
using TheWebApiServer.Data;

namespace TheWebApiServer.Controllers
{
    [ApiController]
    [Route("[Controller]")]
    public class testController : Controller
    {
        private static RecommendationModel _recommendationModel;
        private DataContext _context;
        public testController(RecommendationModel recommendationModel,DataContext dataContext)
        {
            _recommendationModel=recommendationModel;
            /* _recommendationModel.Initialize(dataContext);*/
            _context = dataContext;
        }

        [HttpGet("testGetMusic")]
        [Authorize]
        public async Task<IActionResult> testGetMusic([FromQuery] string userId)
        {
            /*var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
*/
           
            var res = _recommendationModel.GetRecommendationsForUser(_context,userId, 10);

            return Ok();
        }
    }
}
